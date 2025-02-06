package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.io.IOException;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

public class Manager {

    private final Scraper scraper;
    private final Extractor extractor;
    private final JobPostingRepository jobPostingRepository;

    public Manager(Scraper scraper, Extractor extractor, @Autowired JobPostingRepository jobPostingRepository) {
        this.scraper = scraper;
        this.extractor = extractor;
        this.jobPostingRepository = jobPostingRepository;
    }

    public void process(String url) {
        List<JobPostingEntity> jobPostingEntities = processJobScraping(url);
        saveJobPostings(jobPostingEntities);
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
