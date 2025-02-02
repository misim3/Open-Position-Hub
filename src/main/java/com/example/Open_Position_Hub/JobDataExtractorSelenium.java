package com.example.Open_Position_Hub;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JobDataExtractorSelenium {

    public JobDataExtractorSelenium() {
    }

    public Map<String, List<String>> handleFilterBar(String url) {

        // ChromeDriver 경로 설정 (본인의 환경에 맞게 수정)
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\sim00\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

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
            System.out.println("필터 옵션 가져오는 중...");
            filterOptions = getFilterOptions(driver, wait);

        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            driver.quit();  // WebDriver 종료
        }

        return filterOptions;
    }

    // 특정 필터(구분, 직군, 경력사항 등)의 선택지를 가져오는 메서드
    private Map<String, List<String>> getFilterOptions(WebDriver driver,
        WebDriverWait wait) {

        Map<String, List<String>> map = new LinkedHashMap<>();

        try {
            // 필터 항목 추출
            List<WebElement> filters = driver.findElements(
                By.cssSelector("span.sc-86b147bc-0.ghZIoe"));

            for (WebElement filter : filters) {

                String text = filter.getText();
                String name = text.replaceAll("\\s*\\(\\d+\\)", "");

                List<String> options = new ArrayList<>();

                filter.click(); // 드롭다운 열기
                Thread.sleep(500); // 짧은 대기 (드롭다운 포털 생성 시간)

                // 'dropdown-portal'이 생성될 때까지 대기
                WebElement dropdownPortal = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dropdown-portal")));

                // 필터 옵션 가져오기
                List<WebElement> optionElements = dropdownPortal.findElements(By.cssSelector("span.sc-86b147bc-0.ONaOy"));
                for (WebElement option : optionElements) {
                    options.add(option.getText());
                }

                map.put(name, options);
            }

        } catch (Exception e) {
            System.out.println("필터의 선택지들을 가져오지 못했습니다.");
        }
        return map;
    }
}
