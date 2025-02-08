package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.List;
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

    @BeforeEach
    void setUp() {
        jobPostingRepository.deleteAll();
        companyRepository.deleteAll();
    }

    private CompanyEntity doeat() {
        return new CompanyEntity("doeat", "greeting", "https://teamdoeat.career.greetinghr.com");
    }

    private CompanyEntity doodlin() {
        return new CompanyEntity("doodlin", "greeting", "https://www.doodlin.co.kr");
    }

    @Test
    void test() {

        companyRepository.saveAll(List.of(doeat(), doodlin()));

        manager.process();

        jobPostingRepository.findAll().forEach(System.out::println);

    }
}
