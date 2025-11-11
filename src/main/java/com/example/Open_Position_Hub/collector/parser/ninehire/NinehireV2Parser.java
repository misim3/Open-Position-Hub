package com.example.Open_Position_Hub.collector.parser.ninehire;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
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
public class NinehireV2Parser implements JobParser {

    private static final String key = "나인하이어/V2";
    private static final Logger logger = LoggerFactory.getLogger(NinehireV2Parser.class);
    private static final Pattern PREFIX_BRACKET_BLOCKS =
        Pattern.compile("^(?:\\s*(?:\\[[^]]*]|\\([^)]*\\)|\\{[^}]*}|<[^>]*>))+\\s*");

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {

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
            driver.get(company.getRecruitmentUrl());

            Map<String, List<String>> options = handleFilterBar(driver, wait);
            if (options.isEmpty()) {
                logger.error(
                    "HTML structure Error: Unable to find elements in NinehireV2Parser.handleSideBar for Company: {}, URL: {}",
                    company.getName(), company.getRecruitmentUrl());
                return null;
            }

            List<JobPostingDto> jobPostings = handleJobCards(driver, wait, options,
                company.getId());
            if (jobPostings.isEmpty()) {
                logger.error(
                    "HTML structure Error: Unable to find elements in NinehireV2Parser.handleJobCards for Company: {}, URL: {}",
                    company.getName(), company.getRecruitmentUrl());
                return null;
            }
            return jobPostings;

        } catch (Exception e) {
            logger.error(
                "HTML structure Error: Unable to find elements in NinehireV2Parser for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl(), e);
        } finally {
            driver.quit();
        }

        return null;
    }

    private Map<String, List<String>> handleFilterBar(WebDriver driver, WebDriverWait wait) throws RuntimeException {

        Map<String, List<String>> options = new HashMap<>();

        List<WebElement> categories = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.JobPostingsDropdownTypeLayoutHeader__Layout-sc-c5dbb943-0.ldceuE div.Select__Container-sc-e3d3eefb-0.eYiZcn")));

        for (int i = 0; i < categories.size(); i++) {

            WebElement category = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.JobPostingsDropdownTypeLayoutHeader__Layout-sc-c5dbb943-0.ldceuE div.Select__Container-sc-e3d3eefb-0.eYiZcn"))).get(i);

            String name = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(category, By.cssSelector("span.Body-sc-753b8ac7-0.Select__Title-sc-e3d3eefb-4.kTmLlH.hVyNxR"))).getText().replace("전체", "").trim();

            WebElement clickable = category.findElement(By.cssSelector("div.Select__SelectContainer-sc-e3d3eefb-2.kIUWBE.ant-dropdown-trigger"));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", clickable);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", clickable);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.ant-dropdown.ant-dropdown-placement-bottomLeft")));

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.ant-dropdown-menu-item.ant-dropdown-menu-item-active.ant-dropdown-menu-item-only-child.DropdownMenu-sc-51c18b8a-0.jQXtog")));
            List<WebElement> checks = category.findElements(By.cssSelector("li.ant-dropdown-menu-item.ant-dropdown-menu-item-active.ant-dropdown-menu-item-only-child.DropdownMenu-sc-51c18b8a-0.jQXtog"));

            List<String> values = new ArrayList<>();
            for (WebElement check : checks) {

                wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(check, By.cssSelector("span.MenuDropdown__MenuTitle-sc-1a727d3d-7.kKwRqO")));

                WebElement element = check.findElement(By.cssSelector("span.MenuDropdown__MenuTitle-sc-1a727d3d-7.kKwRqO"));

                if (!element.getText().contains(name)) {
                    values.add(element.getText().replaceAll("\\s*\\(\\d+\\)", "").trim());
                }
            }
            options.put(name, values);
            logger.info("name: {}, values: {}", name, values);

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", clickable);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", clickable);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.Select__SelectContainer-sc-e3d3eefb-2.kIUWBE.ant-dropdown-trigger")));

        }

        return options;
    }


    private List<JobPostingDto> handleJobCards(WebDriver driver, WebDriverWait wait,
        Map<String, List<String>> options, Long companyId) throws RuntimeException {

        Set<JobPostingDto> jobPostings = new HashSet<>();

        Map<String, Field> textToField = buildTextToField(options);

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB")));

        while (true) {
            List<WebElement> curCards = driver.findElements(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"));
            WebElement firstCard = curCards.isEmpty() ? null : curCards.get(0);

            List<WebElement> cards = driver.findElements(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"));
            for (int i = 0; i < cards.size(); i++) {

                WebElement card = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"))).get(i);

                String displayTitle = card.findElement(By.cssSelector(
                        "h1.Heading-sc-7076dd01-0.JobPostingsJobPosting__Title-sc-6ae888f2-6.iBwyHC.gtFbzH"))
                    .getText();
                String searchTitle = PREFIX_BRACKET_BLOCKS.matcher(displayTitle)
                    .replaceFirst("");

                List<WebElement> details = card.findElements(By.cssSelector(
                    "span.Body-sc-753b8ac7-0.JobPostingsJobPosting__Tag-sc-6ae888f2-8.iGkPkg.flPAJD"));

                String category = "", experienceLevel = "", employmentType = "", location = "";
                for (WebElement d : details) {
                    String text = d.getText();
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

                String detailUrl = getDetailUrl(driver, wait, card, driver.findElement(By.cssSelector("li.ant-pagination-item-active")).getText().trim());

                jobPostings.add(
                    new JobPostingDto(displayTitle, searchTitle, category, experienceLevel,
                        employmentType, location, detailUrl, companyId));
            }

            WebElement nextLi = driver.findElement(By.cssSelector("li.ant-pagination-next"));
            WebElement nextBtn = nextLi.findElement(
                By.cssSelector("button.ant-pagination-item-link"));

            String aria = nextBtn.getAttribute("aria-disabled");
            if ("true".equals(aria) || nextLi.getAttribute("class")
                .contains("ant-pagination-disabled")) {
                break; // 마지막 페이지
            }

            String prevPageText = driver.findElement(
                By.cssSelector("li.ant-pagination-item-active")).getText().trim();

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'})", nextBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", nextBtn);

            if (firstCard != null) {
                wait.until(ExpectedConditions.stalenessOf(firstCard));
            }
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"), 0));

            wait.until(d -> {
                try {
                    String now = d.findElement(By.cssSelector("li.ant-pagination-item-active"))
                        .getText().trim();
                    return !now.equals(prevPageText);
                } catch (Exception e) {
                    return false;
                }
            });
        }

        return jobPostings.stream().toList();
    }

    private String getDetailUrl(WebDriver driver, WebDriverWait wait, WebElement card, String page) throws RuntimeException {

        String listUrl = driver.getCurrentUrl();

        // 1) 카드 내에서 클릭 타깃 및 href 추출 시도
        //    - a[href]가 있으면 그게 제일 안전 (CSR도 접근성 때문에 대개 a를 둡니다)
//        try {
//            clickable = card.findElement(By.cssSelector("a[href]"));
//        } catch (Exception e) {
//            // a가 없으면 제목 등 클릭 가능한 요소로 대체
//            try {
//                clickable = card.findElement(By.cssSelector(
//                    "h1.Heading-sc-7076dd01-0.JobPostingsJobPosting__Title-sc-6ae888f2-6.iBwyHC.gtFbzH"
//                ));
//            } catch (Exception ex) {
//                // 그래도 없으면 카드 전체 클릭
//                clickable = card;
//            }
//        }
//
//        // href 직접 추출 (closest a 우선)
//        String href = null;
//        try {
//            href = (String) ((JavascriptExecutor) driver).executeScript(
//                "var n=arguments[0]; var a=n.closest('a[href]')||n.querySelector('a[href]'); return a?a.href:null;",
//                clickable
//            );
//        } catch (Exception ignore) {}
//
//        // 2) href가 있으면: 새 탭으로 열어 URL만 가져오기 (부모는 그대로 유지)
//        if (href != null && !href.isBlank()) {
//            Set<String> before = driver.getWindowHandles();
//            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", href);
//
//            String detailUrl = wait.until(d -> {
//                Set<String> after = d.getWindowHandles();
//                if (after.size() > before.size()) {
//                    after.removeAll(before);
//                    String child = after.iterator().next();
//                    d.switchTo().window(child);
//                    return (String) ((JavascriptExecutor) d).executeScript("return window.location.href");
//                }
//                return null;
//            });
//
//            // 닫고 부모로 복귀
//            driver.close();
//            driver.switchTo().window(parent);
//            return detailUrl;
//        }

        // 3) href가 없으면: Ctrl/Command + 클릭으로 새 탭 강제 (앵커가 없어도 라우터가 a로 감싸는 경우 많음)
        {
            Set<String> before = driver.getWindowHandles();
            try {
                // 뷰로 스크롤
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", card);

                // Windows/Linux: CONTROL, macOS: COMMAND 둘 다 시도
                try {
                    new Actions(driver).keyDown(Keys.CONTROL).click(card).keyUp(Keys.CONTROL).perform();
                } catch (Exception e1) {
                    try {
                        new Actions(driver).keyDown(Keys.COMMAND).click(card).keyUp(Keys.COMMAND).perform();
                    } catch (Exception ignore) {}
                }

                wait.until(d -> {
                    Set<String> after = d.getWindowHandles();
                    if (after.size() > before.size()) {
                        after.removeAll(before);
                        String child = after.iterator().next();
                        d.switchTo().window(child);
                    }
                    return null;
                });
//
//                if (detailUrl != null) {
//                    driver.close();
//                    driver.switchTo().window(parent);
//                    return detailUrl;
//                }
            } catch (Exception ignore) {}
        }

        // 4) 마지막 안전망: 같은 탭으로 들어가서 URL 읽고, "뒤로가기 후 원래 페이지 복구"
        //    (이 경로가 문제의 원인이었으므로 복구 루틴을 덧댑니다)
        String detailUrl = (String) ((JavascriptExecutor) driver).executeScript("return window.location.href");
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", clickable);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", clickable);
//
//        // 라우팅 완료까지 대기
//        String detailUrl = wait.until(d -> {
//            String now = (String) ((JavascriptExecutor) d).executeScript("return window.location.href");
//            return now.equals(listUrl) ? null : now;
//        });

        // 뒤로가기
        driver.get(listUrl);

        // 목록 재등장

        while (true) {

            List<WebElement> curCards = driver.findElements(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"));
            WebElement firstCard = curCards.isEmpty() ? null : curCards.get(0);

            By CARD = By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(CARD, 0));

            WebElement nextLi = driver.findElement(By.cssSelector("li.ant-pagination-next"));
            WebElement nextBtn = nextLi.findElement(
                By.cssSelector("button.ant-pagination-item-link"));

            String now = driver.findElement(By.cssSelector("li.ant-pagination-item-active"))
                .getText().trim();

            if (page.equals(now)) {
                break;
            }

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'})", nextBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", nextBtn);

            if (firstCard != null) {
                wait.until(ExpectedConditions.stalenessOf(firstCard));
            }
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("div.JobPostingsJobPosting__Layout-sc-6ae888f2-0.ffnSOB"), 0));

        }

        return detailUrl;
    }

    private Map<String, Field> buildTextToField(Map<String, List<String>> options) {
        Map<String, Field> map = new HashMap<>();
        options.forEach((k, values) -> {
            Field f = switch (k) {
                case "직군" -> Field.CATEGORY;
                case "경력사항", "경력 사항" -> Field.EXPERIENCE;
                case "고용형태", "고용 형태" -> Field.EMPLOYMENT;
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
