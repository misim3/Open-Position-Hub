package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ManagerTest {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private Manager manager;

    @Autowired
    private Environment env;

    private CompanyEntity doeat() {
        return new CompanyEntity("doeat", "그리팅", "https://teamdoeat.career.greetinghr.com/ko/jobposting#323ea93b-ce52-45c9-bbbf-0b85ad135508");
    }

    private CompanyEntity doodlin() {
        return new CompanyEntity("doodlin", "그리팅", "https://www.doodlin.co.kr/ko/career#3276397a-a988-4ca5-ab47-9aa05e9cce30");
    }

    private CompanyEntity gravityLabs() {
        return new CompanyEntity("gravityLabs", "그리팅", "https://gravitylabs.career.greetinghr.com/ko/home#1df7f045-8c3f-48eb-a9f6-a3bd28a1e0e2");
    }

    private CompanyEntity gear2() {
        return new CompanyEntity("gear2", "그리팅", "https://gear2.career.greetinghr.com/ko/career1");
    }

    @Test
    void profileCheck() {
        System.out.println("▶ Active profile = " + System.getProperty("spring.profiles.active"));
    }

    @Test
    void printDataSourceUrl() {
        System.out.println("🔍 spring.datasource.url = " + env.getProperty("spring.datasource.url"));
    }

    @Test
    void test() {

        companyRepository.saveAll(List.of(doeat(), gravityLabs(), doodlin(), gear2()));

        manager.process();

        jobPostingRepository.findAll().forEach(System.out::println);

    }
}
