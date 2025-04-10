package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ManagerTest {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private Manager manager;

    private CompanyEntity doeat() {
        return new CompanyEntity("doeat", "그리팅", "https://teamdoeat.career.greetinghr.com/home#323ea93b-ce52-45c9-bbbf-0b85ad135508");
    }

    private CompanyEntity doodlin() {
        return new CompanyEntity("doodlin", "그리팅", "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30");
    }

    @Test
    void test() {

        companyRepository.deleteAll();
        jobPostingRepository.deleteAll();

        companyRepository.saveAll(List.of(doeat(), doodlin()));

        manager.process();

        jobPostingRepository.findAll().forEach(System.out::println);

        companyRepository.deleteAll();
        jobPostingRepository.deleteAll();

    }
}
