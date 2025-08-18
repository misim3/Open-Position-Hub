package com.example.Open_Position_Hub.collector.parser.Greeting;

import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GreetingV2Parser implements JobParser {

    private static final String key = "그리팅/V2";
    private enum Field { CATEGORY, EXPERIENCE, EMPLOYMENT, LOCATION }
    private static final Logger logger = LoggerFactory.getLogger(GreetingV2Parser.class);

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingEntity> parse(Document doc, CompanyEntity company) {

        Map<String, List<String>> options = handleFilterBar(company.getRecruitmentUrl());

        return handleJobCards(doc.select("div.sc-9b56f69e-0.enoHnQ"), options, company.getId());
    }

    public Map<String, List<String>> handleFilterBar(String url) {

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // 헤드리스 모드 (UI 없이 실행)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // WebDriver 시작
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));  // 명시적 대기 설정

        Map<String, List<String>> filterOptions = null;

        try {
            // 1. 채용 공고 페이지 접속
            driver.get(url);

            // 2. 필터바의 선택지 가져오기 (업데이트된 방식)
            filterOptions = getFilterOptions(driver, wait);

        } catch (TimeoutException e) {
            logger.warn("TimeoutException: Page loading timeout exceeded. URL: {}", url, e);

        } catch (WebDriverException e) {
            logger.error("WebDriverException: Failed to execute WebDriver - URL: {}", url, e);

        } catch (Exception e) {
            logger.error("Unexpected exception occurred - URL: {}", url, e);

        } finally {
            driver.quit();
        }
        return filterOptions;
    }

    // 특정 필터(구분, 직군, 경력사항 등)의 선택지를 가져오는 메서드
    private Map<String, List<String>> getFilterOptions(WebDriver driver,
        WebDriverWait wait) {

        Map<String, List<String>> map = new LinkedHashMap<>();

        try {
            // 필터 항목 추출
            List<WebElement> filters = driver.findElements(By.cssSelector("span.sc-86b147bc-0.ghZIoe"));

            for (WebElement filter : filters) {

                String text = filter.getText();
                String name = text.replaceAll("\\s*\\(\\d+\\)", "");

                List<String> options = new ArrayList<>();

                try {
                    filter.click();

                    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dropdown-portal")));

                    // 옵션들이 "안정"될 때까지 대기
                    List<WebElement> optionElements = waitForStableOptions(
                        driver,
                        By.cssSelector("#dropdown-portal span.sc-86b147bc-0.ONaOy"),
                        10,   // 최대 10번 확인
                        200   // 200ms 간격
                    );

                    for (WebElement option : optionElements) {
                        try {
                            options.add(option.getText());
                        } catch (StaleElementReferenceException e) {
                            logger.warn("옵션 stale됨 → 필터 '{}'", name);
                        }
                    }

                    // 드롭다운 닫기 (외부 클릭 or ESC)
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);

                } catch (Exception e) {
                    logger.error("에러 발생 (필터 '{}')", name, e);
                }

                map.put(name, options);
            }

        } catch (Exception e) {
            logger.error("Unknown error occurred during filter option extraction.");
        }
        return map;
    }

    private List<WebElement> waitForStableOptions(WebDriver driver, By optionSelector, int maxTries, long intervalMs) {
        int sameCount = 0;
        int prevCount = -1;

        for (int i = 0; i < maxTries; i++) {
            List<WebElement> elements = driver.findElements(optionSelector);
            int currCount = elements.size();

            if (currCount == prevCount && currCount > 0) {
                sameCount++;
                if (sameCount >= 2) { // 2회 연속 같으면 안정된 것으로 판단
                    return elements;
                }
            } else {
                sameCount = 0;
            }

            prevCount = currCount;

            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                break;
            }
        }

        return new ArrayList<>();
    }

    private List<JobPostingEntity> handleJobCards(Elements links, Map<String, List<String>> options, Long companyId) {

        List<JobPostingEntity> jobPostingEntities = new ArrayList<>();
        Map<String, Field> textToField = buildTextToField(options);

        for (Element link : links) {
            String href = link.attr("href");
            String title = link.select("span.sc-86b147bc-0.gIOkaZ.sc-d200d649-1.dKCwbm").text();
            Elements details = link.select("span.sc-be6466ed-3.bDOHei");

            String category = "";
            String experienceLevel = "";
            String employmentType = "";
            String location = "";

            for (Element detail : details) {
                String text = detail.text();

                Field f = textToField.get(text);
                if (f != null) {
                    switch (f) {
                        case CATEGORY   -> category = text;
                        case EXPERIENCE -> experienceLevel = text;
                        case EMPLOYMENT -> employmentType = text;
                        case LOCATION   -> location = text;
                    }
                } else if (experienceLevel.isEmpty() && text.contains("경력")) {
                    experienceLevel = text;
                }

                if (!category.isEmpty() && !experienceLevel.isEmpty()
                    && !employmentType.isEmpty() && !location.isEmpty()) {
                    break;
                }
            }

            jobPostingEntities.add(new JobPostingEntity(title, category, experienceLevel, employmentType, location, href, companyId));
        }

        return jobPostingEntities;
    }

    private Map<String, Field> buildTextToField(Map<String, List<String>> options) {
        Map<String, Field> map = new HashMap<>();
        options.forEach((k, values) -> {
            Field f = switch (k) {
                case "직군"   -> Field.CATEGORY;
                case "경력사항" -> Field.EXPERIENCE;
                case "고용형태" -> Field.EMPLOYMENT;
                case "근무지"  -> Field.LOCATION;
                default       -> null;
            };
            if (f != null) {
                for (String v : values) {
                    map.put(v, f); // 필요하면 .trim()·소문자 변환 등 정규화 추가
                }
            }
        });
        return map;
    }
}
