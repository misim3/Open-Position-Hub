package com.example.Open_Position_Hub.collector;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
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
    ApplicationContext context;
    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private Manager manager;
    @Autowired
    private Environment env;

    private CompanyEntity doeat() {
        return new CompanyEntity("doeat", "그리팅",
            "https://teamdoeat.career.greetinghr.com/ko/jobposting#323ea93b-ce52-45c9-bbbf-0b85ad135508");
    }

    private CompanyEntity doodlin() {
        return new CompanyEntity("doodlin", "그리팅",
            "https://www.doodlin.co.kr/ko/career#3276397a-a988-4ca5-ab47-9aa05e9cce30");
    }

    private CompanyEntity gravityLabs() {
        return new CompanyEntity("gravityLabs", "그리팅",
            "https://gravitylabs.career.greetinghr.com/ko/home#1df7f045-8c3f-48eb-a9f6-a3bd28a1e0e2");
    }

    private CompanyEntity gear2() {
        return new CompanyEntity("gear2", "그리팅", "https://gear2.career.greetinghr.com/ko/career1");
    }

    private CompanyEntity weavrcare() {
        return new CompanyEntity("weavrcare", "그리팅",
            "https://weavrcare.career.greetinghr.com/ko/home");
    }

    private CompanyEntity abc1() {
        return new CompanyEntity("abc1", "플랫폼", null);
    }

    private CompanyEntity abc2() {
        return new CompanyEntity("abc2", "그리팅", null);
    }

    private CompanyEntity yogiyo() {
        return new CompanyEntity("yogiyo", "나인하이어", "https://wesangcareer.ninehire.site/");
    }

    private CompanyEntity coinone() {
        return new CompanyEntity("coinone", "나인하이어", "https://recruit.coinonecorp.com/");
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
    void scheduling_should_be_disabled_in_test_profile() {
        // 1) 스케줄러 등록 현황 수집
        Collection<ScheduledTaskHolder> holders = context.getBeansOfType(ScheduledTaskHolder.class)
            .values();
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

        companyRepository.saveAll(List.of(yogiyo(), coinone()));

        manager.scrape();

//        manager.check();

        jobPostingRepository.findAll().forEach(System.out::println);

    }

    @Test
    @DisplayName("scrape() 안정성 샘플링(10회) - 예외 비율 리포트")
    void scrape_stability_sampling_10() {
        final int RUNS = 10;

        int exceptions = 0;
        List<String> errorSummaries = new ArrayList<>();

        for (int i = 1; i <= RUNS; i++) {
            // 각 회차 간 간섭 최소화를 위해 데이터 정리 (필요 시 유지/삭제 선택)
            jobPostingRepository.deleteAllInBatch();
            companyRepository.deleteAllInBatch();

            try {
                companyRepository.save(coinone());
                manager.scrape();
//              manager.check(); // 필요 시 포함

                // 결과를 실제로 터치해 I/O 경로를 동일하게
                long count = jobPostingRepository.count();
                System.out.printf("[Run %02d] OK - jobPosting count=%d%n", i, count);

            } catch (Throwable t) {
                exceptions++;
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                String stack = sw.toString();

                // 한 줄 요약 + 상위 스택 몇 줄 추림
                String summary = String.format(
                    "[Run %02d] FAILED - %s: %s%n%s",
                    i,
                    t.getClass().getSimpleName(),
                    t.getMessage(),
                    trimStack(stack, 12) // 보여줄 스택 줄 수
                );

                errorSummaries.add(summary);
                System.err.println(summary);
            }
        }

        double rate = (exceptions * 100.0) / RUNS;
        System.out.printf("%n==== Summary ====%n");
        System.out.printf("Total: %d runs, Exceptions: %d (%.1f%%)%n", RUNS, exceptions, rate);

        if (!errorSummaries.isEmpty()) {
            System.out.println("\n---- Error samples ----");
            errorSummaries.forEach(s -> System.out.println(s + "\n"));
        }

        // 실패율 임계치 검증이 필요하면 주석 해제 (예: 20% 이하 기대)
        // assertTrue(rate <= 20.0, "Exception rate too high: " + rate + "%");
    }

    /**
     * 스택트레이스를 상위 N줄만 보여주기 위한 헬퍼.
     */
    private static String trimStack(String full, int maxLines) {
        String[] lines = full.split("\\R");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
            sb.append(lines[i]).append(System.lineSeparator());
        }
        if (lines.length > maxLines) sb.append("... (truncated)").append(System.lineSeparator());
        return sb.toString();
    }
}
