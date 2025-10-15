package com.example.Open_Position_Hub.collector;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
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

    private CompanyEntity weavrcare() {
        return new CompanyEntity("weavrcare", "그리팅", "https://weavrcare.career.greetinghr.com/ko/home");
    }

    private CompanyEntity abc1() {
        return new CompanyEntity("abc1", "플랫폼", null);
    }

    private CompanyEntity abc2() {
        return new CompanyEntity("abc2", "그리팅", null);
    }

    @Test
    void profileCheck() {
        System.out.println("▶ Active profile = " + System.getProperty("spring.profiles.active"));
    }

    @Test
    void printDataSourceUrl() {
        System.out.println("🔍 spring.datasource.url = " + env.getProperty("spring.datasource.url"));
    }
    @Autowired
    ApplicationContext context;

    @Test
    void scheduling_should_be_disabled_in_test_profile() {
        // 1) 스케줄러 등록 현황 수집
        Collection<ScheduledTaskHolder> holders = context.getBeansOfType(ScheduledTaskHolder.class).values();
        Set<ScheduledTask> tasks = holders.stream()
            .flatMap(h -> h.getScheduledTasks().stream())
            .collect(Collectors.toSet());

        // 2) 조건: (a) 아예 홀더가 없거나, (b) 홀더는 있어도 등록된 작업이 0
        boolean disabled = holders.isEmpty() || tasks.isEmpty();

        // 3) 실패 시 어떤 작업이 잡혀 있었는지 보여주기 (toString()에 크론/딜레이 정보가 포함됨)
        String debug = tasks.stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n"));

        assertTrue(disabled, () ->
            "스케줄링이 활성화되어 있습니다. 등록된 작업:\n" + (debug.isBlank() ? "(없음)" : debug));
    }

    @Test
    void test() {

        companyRepository.saveAll(List.of(doeat(), abc1(), abc2()));

        manager.scrape();

        manager.check();

        jobPostingRepository.findAll().forEach(System.out::println);

    }
}
