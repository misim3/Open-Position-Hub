package com.example.Open_Position_Hub.collector.parser.ninehire;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NinehireV1Parser implements JobParser {

    private static final String key = "나인하이어/V1";
    private static final Logger logger = LoggerFactory.getLogger(NinehireV1Parser.class);
    private static final Pattern PREFIX_BRACKET_BLOCKS =
        Pattern.compile("^(?:\\s*(?:\\[[^]]*]|\\([^)]*\\)|\\{[^}]*}|<[^>]*>))+\\s*");

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {

        Map<String, List<String>> options = handleSideBar(
            doc.select("div.JobPostingsSidebarFilters__FiltersLayout-sc-2062e5e7-0.hMKvYs")
                .select("div.JobPostingsSidebarFilter__Layout-sc-6112d8f1-0.khLaJL"));
        if (options.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in NinehireV1Parser.handleSideBar for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }

        Element container = doc.selectFirst(
            "div.JobPostingsSidebarTypeLayoutBody__Layout-sc-c28e05fb-0.gFjOAV");
        if (container == null) {
            logger.error("Element not found in NinehireV1Parser.parse.container for Company: {}",
                company.getName());
            return null;
        }

        List<JobPostingDto> jobPostings = handleJobCards(container, options, company.getId(),
            company.getRecruitmentUrl());
        if (jobPostings.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in NinehireV1Parser.handleJobCards for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }
        return jobPostings;
    }

    private Map<String, List<String>> handleSideBar(Elements categories) {

        Map<String, List<String>> options = new HashMap<>();

        for (Element category : categories) {
            String name = category.select(
                    "span.Body-sc-b30a2c4-0.JobPostingsSidebarFilter__Title-sc-6112d8f1-4.kOYzOz")
                .text();

            Elements checks = category.select(
                "div.JobPostingsSidebarFilter__FilterRadioLayout-sc-6112d8f1-8.emJXFB");

            List<String> values = new ArrayList<>();
            for (Element check : checks) {
                values.add(check.select(
                        "span.Body-sc-b30a2c4-0.JobPostingsSidebarFilter__FilterRadioTitle-sc-6112d8f1-13.kHpiRr.deIPOu")
                    .text());
            }
            options.put(name, values);
        }

        return options;
    }

    private List<JobPostingDto> handleJobCards(Element container, Map<String, List<String>> options,
        Long companyId, String url) {

        List<JobPostingDto> jobPostings = new ArrayList<>();

        Map<String, Field> textToField = buildTextToField(options);

        // Chrome 옵션 설정
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");  // 헤드리스 모드 (UI 없이 실행)
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            driver.get(url);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB")));

            while (true) {
                Elements cards = container.select(
                    "div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB");

                for (Element card : cards) {
                    String detailUrl = getDetailUrl(driver, wait, card);
                    String displayTitle = card.select(
                            "h1.Heading-sc-7076dd01-0.JobPostingsJobPosting__Title-sc-6ae888f2-6.iBwyHC.gtFbzH")
                        .text();
                    String searchTitle = PREFIX_BRACKET_BLOCKS.matcher(displayTitle)
                        .replaceFirst("");

                    Elements details = card.select(
                        "span.Body-sc-b30a2c4-0.JobPostingsJobPosting__Tag-sc-6ae888f2-8 kHpiRr.flPAJD");

                    String category = "";
                    String experienceLevel = "";
                    String employmentType = "";
                    String location = "";

                    for (Element detail : details) {
                        String text = detail.text();

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
                            && !employmentType.isEmpty() && !location.isEmpty()) {
                            break;
                        }
                    }

                    jobPostings.add(
                        new JobPostingDto(displayTitle, searchTitle, category, experienceLevel,
                            employmentType, location, null,
                            companyId));
                }

                WebElement next = driver.findElement(By.cssSelector(
                    "div.JobPostingsPagination__StyleWrapper-sc-2cfa53e4-0.jQRYFm li.ant-pagination-next"));

                if (next.getAttribute("aria-disabled").equals("true")) {
                    break;
                }

                String prev = driver.findElement(By.cssSelector("li.ant-pagination-item-active"))
                    .getText().trim();

                driver.findElement(
                        By.cssSelector("li.ant-pagination-next button.ant-pagination-item-link"))
                    .click();

                wait.until(d -> {
                    try {
                        String now = d.findElement(By.cssSelector("li.ant-pagination-item-active"))
                            .getText().trim();
                        return !now.equals(prev);
                    } catch (Exception e) {
                        return false;
                    }
                });

            }

        } catch (Exception e) {
            logger.error("Fail to handleJobCards in NinehireV1Parser: {}", e.getMessage());
        } finally {
            driver.quit();
        }

        return jobPostings;
    }

    private String getDetailUrl(WebDriver driver, WebDriverWait wait, Element card) {
        return "detailUrl";
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
