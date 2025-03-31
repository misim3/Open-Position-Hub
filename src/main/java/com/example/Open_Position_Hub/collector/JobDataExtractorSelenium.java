package com.example.Open_Position_Hub.collector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class JobDataExtractorSelenium {

    private static final Logger logger = LoggerFactory.getLogger(JobDataExtractorSelenium.class);

    public Map<String, List<String>> handleFilterBar(String url) {

        // ChromeDriver 경로 설정 (본인의 환경에 맞게 수정)
        String driverPath = System.setProperty("webdriver.chrome.driver", "C:\\Users\\sim00\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        if (driverPath == null || driverPath.isBlank()) {
            logger.error("ChromeDriver path is not set. Please configure the system property: 'webdriver.chrome.driver'.");
        }

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");  // 헤드리스 모드 (UI 없이 실행)
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

}
