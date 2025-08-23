package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.collector.platform.PlatformRegistry;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    public Manager(Scraper scraper,
        JobPostingRepository jobPostingRepository,
        CompanyRepository companyRepository,
        PlatformRegistry platformRegistry) {
        this.scraper = scraper;
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
        this.platformRegistry = platformRegistry;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void process() {
        System.out.println("Processing...");
//        List<CompanyEntity> companies = companyRepository.findAll();
        List<CompanyEntity> companies = companyRepository.findAllByRecruitmentPlatform("그리팅");
        companies.forEach(company -> saveJobPostings(processJobScraping(company)));
        System.out.println("Done!");
    }

    private List<JobPostingEntity> processJobScraping(CompanyEntity company) {

        logger.info("{} Processing job scraping...", company.getName());

        Optional<Document> doc = scraper.fetchHtml(company.getRecruitmentUrl());

        if (doc.isPresent()) {
            return platformRegistry.getStrategy(company.getRecruitmentPlatform()).scrape(doc.get(), company);
        }

        return List.of();
    }

    private void saveJobPostings(List<JobPostingEntity> scrapedJobPostings) {

        if (scrapedJobPostings.isEmpty()) {
            return;
        }

        Long companyId = scrapedJobPostings.get(0).getCompanyId();

        List<JobPostingEntity> existing = jobPostingRepository.findByCompanyId(companyId);
        Set<String> existingUrls = existing.stream()
            .map(JobPostingEntity::getDetailUrl)
            .collect(Collectors.toSet());

        Map<String, JobPostingEntity> scrapedByUrl = new LinkedHashMap<>();
        for (JobPostingEntity s : scrapedJobPostings) {
            scrapedByUrl.putIfAbsent(s.getDetailUrl(), s);
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
}
