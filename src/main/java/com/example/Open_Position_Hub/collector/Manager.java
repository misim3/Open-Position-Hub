package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Manager {

    private static final Logger logger = LoggerFactory.getLogger(Manager.class);
    private final Scraper scraper;
    private final Extractor extractor;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;

    public Manager(Scraper scraper, Extractor extractor,
        @Autowired JobPostingRepository jobPostingRepository,
        @Autowired CompanyRepository companyRepository) {
        this.scraper = scraper;
        this.extractor = extractor;
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
    }

    @Scheduled(fixedRate = 86400000) // 24시간 마다 실행
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
            if (company.getRecruitmentPlatform().equals("그리팅")) {
                return extractor.extractGreeting2(doc.get(), company.getRecruitmentUrl(),
                    company.getId());
            } else {
                logger.warn("Unsupported platform: {}", company.getRecruitmentPlatform());
            }
        }

        return List.of();
    }

    private void saveJobPostings(List<JobPostingEntity> scrapedJobPostings) {

        if (scrapedJobPostings.isEmpty()) {
            return;
        }

        List<String> existingUrls = jobPostingRepository.findByCompanyId(scrapedJobPostings.get(0).getCompanyId()).stream()
            .map(JobPostingEntity::getDetailUrl)
            .toList();

        List<JobPostingEntity> newJobPostings = scrapedJobPostings.stream()
            .filter(job -> !existingUrls.contains(job.getDetailUrl()))
            .toList();

        if (!newJobPostings.isEmpty()) {
            jobPostingRepository.saveAll(newJobPostings);
            logger.info("Saved {} new job postings.", newJobPostings.size());
        } else {
            logger.info("No new job postings to save. All are duplicates.");
        }
    }
}
