package com.example.Open_Position_Hub.collector.parser.Greeting;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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
    private static final Logger logger = LoggerFactory.getLogger(GreetingV2Parser.class);
    private static final Pattern PREFIX_BRACKET_BLOCKS =
        Pattern.compile("^(?:\\s*(?:\\[[^]]*]|\\([^)]*\\)|\\{[^}]*}|<[^>]*>))+\\s*");


    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {

        Map<String, List<String>> options = handleFilterBar(company.getRecruitmentUrl());
        if (options.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in GreetingV2Parser.handleSideBar for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }

        Element container = doc.selectFirst("div.sc-9b56f69e-0.enoHnQ");
        if (container == null) {
            logger.error("Element not found in GreetingV2Parser.parse.container for Company: {}",
                company.getName());
            return null;
        }

        List<JobPostingDto> jobPostings = handleJobCards(container, options, company.getId());
        if (jobPostings.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in GreetingV2Parser.handleJobCards for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }
        return jobPostings;
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

            // 2. 팝업창 닫기
            closePopupIfPresent(driver, wait);

            // 3. 필터바의 선택지 가져오기
            getFilterOptions(driver, wait, filterOptions);

        } catch (TimeoutException e) {
            logger.warn("TimeoutException: Page loading timeout exceeded. URL: {}, {}", url,
                e.getMessage());

        } catch (WebDriverException e) {
            logger.error("WebDriverException: Failed to execute WebDriver - URL: {}, {}", url,
                e.getMessage());

        } catch (Exception e) {
            logger.error("Unexpected exception occurred - URL: {}, {}", url, e.getMessage());

        } finally {
            driver.quit();
        }
        return filterOptions;
    }

    private void closePopupIfPresent(WebDriver driver, WebDriverWait wait) {

        WebDriverWait quickWait = new WebDriverWait(driver, Duration.ofMillis(500));
        try {
            quickWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.modal")));
        } catch (TimeoutException e) {
            return;
        }

        try {

            WebElement element = driver.findElement(By.cssSelector("div.modal"));

            WebElement clickable = element.findElement(By.cssSelector("span.pop_bt"));

            wait.until(ExpectedConditions.elementToBeClickable(clickable));

            try {
                new Actions(driver).moveToElement(clickable).click().perform();
            } catch (WebDriverException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
            }

            wait.until(ExpectedConditions.invisibilityOf(element));

        } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
            logger.error("Fail to close Popup in GreetingV2Parser.closePopupIfPresent {}",
                e.getMessage());
        }
    }

    // 특정 필터(구분, 직군, 경력사항 등)의 선택지를 가져오는 메서드
    private void getFilterOptions(WebDriver driver, WebDriverWait wait,
        Map<String, List<String>> filterOptions) {
        // 필터 라벨(span) 로드 대기
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
            By.cssSelector("span.sc-86b147bc-0.ghZIoe")));

        for (int i = 0; ; i++) {
            List<WebElement> labels = driver.findElements(
                By.cssSelector("span.sc-86b147bc-0.ghZIoe"));
            // 표시 중인 라벨만 대상으로
            labels = labels.stream().filter(WebElement::isDisplayed).toList();
            if (i >= labels.size()) {
                break; // 종료
            }

            WebElement label = labels.get(i);
            String text = label.getText();
            String name = text.replaceAll("\\s*\\(\\d+\\)", "").trim();

            // 실제 클릭 가능한 조상(button/role=button) 우선 사용
            WebElement clickable = label;
            try {
                clickable = label.findElement(
                    By.xpath("./ancestor-or-self::*[self::button or @role='button'][1]"));
            } catch (NoSuchElementException ignore) {
            }

            // 클릭 시도 (일반 → JS 대체)
            try {
                try {
                    new Actions(driver).moveToElement(clickable).pause(Duration.ofMillis(80))
                        .click().perform();
                } catch (ElementNotInteractableException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
                }
            } catch (StaleElementReferenceException e) {
                // 한 번만 재시도
                labels = driver.findElements(By.cssSelector("span.sc-86b147bc-0.ghZIoe")).stream()
                    .filter(WebElement::isDisplayed).toList();
                if (i < labels.size()) {
                    label = labels.get(i);
                    try {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
                    } catch (Exception ignore2) { /* 다음으로 진행 */ }
                }
            }

            // 드롭다운 로드/가시성 대기
            WebElement dropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#dropdown-portal")));

            List<WebElement> optionElements = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(
                        "#dropdown-portal span.sc-86b147bc-0.ONaOy, #dropdown-portal [role='menuitem'], #dropdown-portal li")
                )
            );

            List<String> options = new ArrayList<>();
            for (WebElement op : optionElements) {
                if (op.isDisplayed()) {
                    String val = op.getText().trim();
                    if (!val.isEmpty()) {
                        options.add(val);
                    }
                }
            }
            filterOptions.put(name, options);

            // 드롭다운 닫기
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }

    private List<JobPostingDto> handleJobCards(Element container, Map<String, List<String>> options,
        Long companyId) {

        List<JobPostingDto> jobPostings = new ArrayList<>();

        Map<String, Field> textToField = buildTextToField(options);

        Elements links = container.select("ul a[href]");

        for (Element link : links) {
            String href = link.attr("href");
            String displayTitle = link.select("span.sc-86b147bc-0.gIOkaZ.sc-f484a550-1.gMeHeg").text();
            String searchTitle = PREFIX_BRACKET_BLOCKS.matcher(displayTitle).replaceFirst("");

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
                        case CATEGORY -> category = text;
                        case EXPERIENCE -> experienceLevel = text;
                        case EMPLOYMENT -> employmentType = text;
                        case LOCATION -> location = text;
                    }
                } else if (experienceLevel.isEmpty() && text.contains("경력")) {
                    experienceLevel = text;
                }

                if (!category.isEmpty() && !experienceLevel.isEmpty()
                    && !employmentType.isEmpty() && !location.isEmpty() && f != null) {
                    break;
                }
            }

            jobPostings.add(
                new JobPostingDto(displayTitle, searchTitle, category, experienceLevel, employmentType, location, href,
                    companyId));
        }

        return jobPostings;
    }

    private Map<String, Field> buildTextToField(Map<String, List<String>> options) {
        Map<String, Field> map = new HashMap<>();
        options.forEach((k, values) -> {
            Field f = switch (k) {
                case "직군" -> Field.CATEGORY;
                case "경력사항" -> Field.EXPERIENCE;
                case "고용형태" -> Field.EMPLOYMENT;
                case "근무지" -> Field.LOCATION;
                default -> null;
            };
            if (f != null) {
                for (String v : values) {
                    map.put(v, f); // 필요하면 .trim()·소문자 변환 등 정규화 추가
                }
            }
        });
        return map;
    }

    private enum Field {CATEGORY, EXPERIENCE, EMPLOYMENT, LOCATION}
}
