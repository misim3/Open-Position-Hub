package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.collector.checker.DeadLinkChecker;
import com.example.Open_Position_Hub.collector.platform.PlatformRegistry;
import com.example.Open_Position_Hub.db.BaseEntity;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Manager {

    private static final Logger logger = LoggerFactory.getLogger(Manager.class);
    private final Scraper scraper;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final PlatformRegistry platformRegistry;
    private final DeadLinkChecker deadLinkChecker;

    private final AtomicInteger counterForCheck;

    public Manager(Scraper scraper,
        JobPostingRepository jobPostingRepository,
        CompanyRepository companyRepository,
        PlatformRegistry platformRegistry,
        DeadLinkChecker deadLinkChecker) {
        this.scraper = scraper;
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
        this.platformRegistry = platformRegistry;
        this.deadLinkChecker = deadLinkChecker;
        this.counterForCheck = new AtomicInteger(0);
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void scrape() {
        System.out.println("Start scraping...");
//        List<CompanyEntity> companies = companyRepository.findAll();
        List<CompanyEntity> companies = companyRepository.findAllByRecruitmentPlatform("그리팅");
        companies.forEach(company -> saveJobPostings(processJobScraping(company)));
        System.out.println("Done!");
    }

    private List<JobPostingDto> processJobScraping(CompanyEntity company) {

        logger.info("{} Processing job scraping...", company.getName());

        Optional<Document> doc = scraper.fetchHtml(company.getRecruitmentUrl());

        if (doc.isPresent()) {
            return platformRegistry.getStrategy(company.getRecruitmentPlatform())
                .scrape(doc.get(), company);
        }

        return List.of();
    }

    private void saveJobPostings(List<JobPostingDto> scrapedJobPostings) {

        if (scrapedJobPostings.isEmpty()) {
            return;
        }

        List<JobPostingDto> jobPostings = new HashSet<>(scrapedJobPostings).stream().toList();

        Long companyId = jobPostings.get(0).companyId();

        List<JobPostingEntity> existing = jobPostingRepository.findByCompanyId(companyId);
        Set<String> existingUrls = existing.stream()
            .map(JobPostingEntity::getDetailUrl)
            .collect(Collectors.toSet());

        Map<String, JobPostingEntity> scrapedByUrl = new LinkedHashMap<>();
        for (JobPostingDto s : scrapedJobPostings) {
            scrapedByUrl.putIfAbsent(s.detailUrl(), s.toEntity());
        }
        Set<String> scrapedUrls = scrapedByUrl.keySet();

        List<JobPostingEntity> toInsert = scrapedByUrl.entrySet().stream()
            .filter(e -> !existingUrls.contains(e.getKey()))
            .map(Map.Entry::getValue)
            .toList();

        List<JobPostingEntity> toDelete = existing.stream()
            .filter(e -> !scrapedUrls.contains(e.getDetailUrl()))
            .toList();

        if (!scrapedJobPostings.isEmpty()) {
            jobPostingRepository.saveAll(toInsert);
            logger.info("Inserted {} new job postings.", toInsert.size());
        } else {
            logger.info("No new job postings to insert.");
        }

        if (!toDelete.isEmpty()) {
            jobPostingRepository.deleteAllInBatch(toDelete);
            logger.info("Removed {} deleted job postings.", toDelete.size());
        } else {
            logger.info("No deleted job postings.");
        }
    }

    @Scheduled(cron = "0 */10 6-23 * * *", zone = "Asia/Seoul")
    public void check() {
        System.out.println("Start checking...");
        int bucket = counterForCheck.getAndUpdate(c -> (c + 1) % 36);

        List<JobPostingEntity> jobPostingEntities = jobPostingRepository.findShard(36, bucket);

        if (jobPostingEntities.isEmpty()) {
            return;
        }

        Map<String, Long> checkUrlToEntityId = new HashMap<>(jobPostingEntities.size());
        List<JobPostingDto> jobPostingsForCheck = new ArrayList<>(jobPostingEntities.size());
        Set<Long> companyIds = jobPostingEntities.stream().map(JobPostingEntity::getCompanyId).collect(Collectors.toSet());
        Map<Long, CompanyEntity> companyMap = companyRepository.findAllById(companyIds).stream()
            .collect(Collectors.toMap(BaseEntity::getId, company -> company));

        for (JobPostingEntity j : jobPostingEntities) {
            CompanyEntity company = companyMap.get(j.getCompanyId());

            if (company == null) {
                logger.warn("[Manager - check] Company id {} not found.", j.getCompanyId());
                continue;
            }

            String checkUrl = buildRedirectUrl(company.getRecruitmentUrl(), j.getDetailUrl());

            checkUrlToEntityId.put(checkUrl, j.getId());

            jobPostingsForCheck.add(new JobPostingDto(
                j.getTitle(),
                j.getCategory(),
                j.getExperienceLevel(),
                j.getEmploymentType(),
                j.getLocation(),
                checkUrl,
                company.getId()
            ));
        }

        if (jobPostingsForCheck.isEmpty()) {
            return;
        }

        List<JobPostingDto> deadJobPostings = deadLinkChecker.checkDeadLinks(jobPostingsForCheck);
        if (deadJobPostings.isEmpty()) {
            return;
        }

        List<Long> idsToDelete = deadJobPostings.stream()
            .map(d -> checkUrlToEntityId.get(d.detailUrl()))
            .filter(Objects::nonNull)
            .toList();

        if (!idsToDelete.isEmpty()) {
            deleteChunk(idsToDelete, 500);
            logger.info("[Manager - check] Deleted {} dead postings", idsToDelete.size());
        }

        System.out.println("Done!");
    }

    private String buildRedirectUrl(String recruitmentUrl, String detailUrl) {
        try {
            URI base = new URI(recruitmentUrl);
            if (!base.getPath().endsWith("/")) {
                base = base.resolve(base.getPath() + "/");
            }
            URI detail = new URI(detailUrl);
            return base.resolve(detail).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid BASE URL: " + recruitmentUrl + ", DETAIL: " + detailUrl, e.fillInStackTrace());
        }
    }

    private void deleteChunk(List<Long> ids, int chunkSize) {
        for (int i = 0; i < ids.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, ids.size());
            jobPostingRepository.deleteAllByIdInBatch(ids.subList(i, end));
        }
    }
}
