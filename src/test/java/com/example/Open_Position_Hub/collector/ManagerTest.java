package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ManagerTest {

    @Autowired
    private JobPostingRepository jobPostingRepository;

//    @Test
//    void process_greeting_typeA() {
//        String url = "https://teamdoeat.career.greetinghr.com";
//        Scraper scraper = new Scraper();
//        Extractor extractor = new Extractor(new CssSelector(), new JobDataExtractorSelenium());
//        Manager manager = new Manager(scraper, extractor, jobPostingRepository);
//
//        manager.process(url);
//
//        List<JobPostingEntity> jobPostingEntities = jobPostingRepository.findAll();
//        jobPostingEntities.forEach(System.out::println);
//    }
//
//    @Test
//    void process_greeting_typeB() {
//        String url = "https://www.doodlin.co.kr";
//        Scraper scraper = new Scraper();
//        Extractor extractor = new Extractor(new CssSelector(), new JobDataExtractorSelenium());
//        Manager manager = new Manager(scraper, extractor, jobPostingRepository);
//
//        manager.process(url);
//
//        List<JobPostingEntity> jobPostingEntities = jobPostingRepository.findAll();
//        jobPostingEntities.forEach(System.out::println);
//    }
}
