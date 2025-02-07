package com.example.Open_Position_Hub.collector;

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

    public Manager(Scraper scraper, Extractor extractor, @Autowired JobPostingRepository jobPostingRepository) {
        this.scraper = scraper;
        this.extractor = extractor;
        this.jobPostingRepository = jobPostingRepository;
    }

    @Scheduled(fixedRate = 86400000) // 24시간 마다 실행
    public void process() {
        System.out.println("Processing...");
        List<JobPostingEntity> jobPostingEntities = processJobScraping("https://teamdoeat.career.greetinghr.com");
        saveJobPostings(jobPostingEntities);
        System.out.println("Done!");
    }

    private List<JobPostingEntity> processJobScraping(String url) {

        try {
            Document doc = scraper.fetchHtml(url);
            return extractor.extractGreeting2(doc, url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveJobPostings(List<JobPostingEntity> jobPostingEntities) {
        jobPostingRepository.saveAll(jobPostingEntities);
    }
}
