package com.example.Open_Position_Hub.collector.detect.ninehire;

import com.example.Open_Position_Hub.collector.detect.DetectorDto;
import com.example.Open_Position_Hub.collector.detect.LayoutDetector;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NinehireV1Detector implements LayoutDetector {

    private static final String key = "나인하이어";
    private static final Logger logger = LoggerFactory.getLogger(NinehireV1Detector.class);

    @Override
    public String platformKey() {
        return key;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String detect(DetectorDto dto) {

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
            driver.get(dto.url());

            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.JobPostingsSidebarTypeLayout__Layout-sc-eeabc865-0.dQMlGW")));

            driver.findElement(
                By.cssSelector("div.JobPostingsSidebarTypeLayout__Layout-sc-eeabc865-0.dQMlGW"));

            return platformKey() + "/V1";
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            driver.quit();
        }

        return null;
    }
}
