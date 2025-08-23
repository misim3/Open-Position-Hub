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
import java.util.Objects;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
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
        if (options.isEmpty()) {
            logger.error("HTML structure changed: Unable to find elements(handleFilterBar) for Company: {}, URL: {}", company.getName(), company.getRecruitmentUrl());
            return List.of();
        }

        List<JobPostingEntity> jobPostingEntities = handleJobCards(
            Objects.requireNonNull(doc.selectFirst("div.sc-9b56f69e-0.enoHnQ")), options, company.getId());
        if (jobPostingEntities.isEmpty()) {
            logger.error("HTML structure changed: Unable to find elements(handleJobCards) for Company: {}, URL: {}", company.getName(), company.getRecruitmentUrl());
        }
        return jobPostingEntities;
    }

    public Map<String, List<String>> handleFilterBar(String url) {

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // 헤드리스 모드 (UI 없이 실행)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // WebDriver 시작
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));  // 명시적 대기 설정

        Map<String, List<String>> filterOptions = new LinkedHashMap<>();

        if (url == null || url.isEmpty()) {
            driver.quit();
            return filterOptions;
        }

        try {
            // 1. 채용 공고 페이지 접속
            driver.get(url);

            // 2. 필터바의 선택지 가져오기 (업데이트된 방식)
            getFilterOptions(driver, wait, filterOptions);

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
    private void getFilterOptions(WebDriver driver, WebDriverWait wait, Map<String, List<String>> filterOptions) {
        // 필터 라벨(span) 로드 대기
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("span.sc-86b147bc-0.ghZIoe")));

        for (int i = 0; ; i++) {
            List<WebElement> labels = driver.findElements(By.cssSelector("span.sc-86b147bc-0.ghZIoe"));
            // 표시 중인 라벨만 대상으로
            labels = labels.stream().filter(WebElement::isDisplayed).toList();
            if (i >= labels.size()) break; // 종료

            WebElement label = labels.get(i);
            String text = label.getText();
            String name = text.replaceAll("\\s*\\(\\d+\\)", "").trim();

            // 실제 클릭 가능한 조상(button/role=button) 우선 사용
            WebElement clickable = label;
            try {
                clickable = label.findElement(By.xpath("./ancestor-or-self::*[self::button or @role='button'][1]"));
            } catch (NoSuchElementException ignore) { }

            // 클릭 시도 (일반 → JS 대체)
            try {
                try {
                    new Actions(driver).moveToElement(clickable).pause(Duration.ofMillis(80)).click().perform();
                } catch (ElementNotInteractableException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
                }
            } catch (StaleElementReferenceException e) {
                // 한 번만 재시도
                labels = driver.findElements(By.cssSelector("span.sc-86b147bc-0.ghZIoe")).stream()
                    .filter(WebElement::isDisplayed).toList();
                if (i < labels.size()) {
                    label = labels.get(i);
                    try { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label); }
                    catch (Exception ignore2) { /* 다음으로 진행 */ }
                }
            }

            // 드롭다운 로드/가시성 대기
            WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#dropdown-portal")));

            List<WebElement> optionElements = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("#dropdown-portal span.sc-86b147bc-0.ONaOy, #dropdown-portal [role='menuitem'], #dropdown-portal li")
                )
            );

            List<String> options = new ArrayList<>();
            for (WebElement op : optionElements) {
                if (op.isDisplayed()) {
                    String val = op.getText().trim();
                    if (!val.isEmpty()) options.add(val);
                }
            }
            filterOptions.put(name, options);

            // 드롭다운 닫기
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }

    private List<JobPostingEntity> handleJobCards(Element container, Map<String, List<String>> options, Long companyId) {

        List<JobPostingEntity> jobPostingEntities = new ArrayList<>();

        Map<String, Field> textToField = buildTextToField(options);

        Elements links = container.select("ul a[href]");

        for (Element link : links) {
            String href = link.attr("href");
            String title = link.select("span.sc-86b147bc-0.gIOkaZ.sc-f484a550-1.gMeHeg").text();
            Elements details = link.select("span.sc-86b147bc-0.bugutw.sc-708ae078-1.gAEjfw");

            String category = "";
            String experienceLevel = "";
            String employmentType = "";
            String location = "";

            for (Element detail : details) {
                String text = detail.select("span.sc-708ae078-3.hBUoLe").text();

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
                    && !employmentType.isEmpty() && !location.isEmpty() && f != null) {
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
