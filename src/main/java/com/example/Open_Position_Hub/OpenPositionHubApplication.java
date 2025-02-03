package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenPositionHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenPositionHubApplication.class, args);

		/*
		큰 구조는 큰 컨테이너가 존재해서 각 모듈 호출
		각 모듈은 스크래퍼, 셀렉터, 추출기, 변환기, 레포지토리.
		 */

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scraper scraper = new Scraper();
        String urlCompanyD1_B = "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30";
        String urlCompanyD2_A = "https://teamdoeat.career.greetinghr.com/home#323ea93b-ce52-45c9-bbbf-0b85ad135508";

        Extractor extractor = new Extractor(new CssSelector(), new JobDataExtractorSelenium());
        JobPostingRepository jobPostingRepository = new JobPostingMemoryRepository();
		int size1 = 0;

        try {
            Document doc = scraper.fetchHtml(urlCompanyD2_A);
            List<JobPosting> jobPostingList1 = extractor.extractGreeting2(doc, urlCompanyD2_A);
            jobPostingRepository.saveAll(jobPostingList1);
            size1 = jobPostingList1.size();
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }

        List<JobPosting> jobPostingList2 = jobPostingRepository.getAllJobPostingList();

        jobPostingList2.forEach(System.out::println);

        System.out.println("size1 = " + size1 + " size2 = " + jobPostingList2.size());

    }

}
