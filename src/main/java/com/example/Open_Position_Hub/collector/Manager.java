package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.io.IOException;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Manager {

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
        List<CompanyEntity> companies = companyRepository.findAll();
        companies.forEach(company -> saveJobPostings(processJobScraping(company)));
        System.out.println("Done!");
    }

    private List<JobPostingEntity> processJobScraping(CompanyEntity company) {

        try {
            Document doc = scraper.fetchHtml(company.getRecruitmentUrl());
            if (company.getRecruitmentPlatform().equals("greeting")) {
                return extractor.extractGreeting2(doc, company.getRecruitmentUrl(),
                    company.getId());
            } else {
                System.out.println("this recruitment platform(" + company.getRecruitmentPlatform()
                    + ") is not supported now.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return List.of();
    }

    private void saveJobPostings(List<JobPostingEntity> jobPostingEntities) {
        jobPostingRepository.saveAll(jobPostingEntities);
    }
}
