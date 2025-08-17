package com.example.Open_Position_Hub.fetch;

import java.time.Duration;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumFetcher implements HtmlFetcher {

    @Override
    public Document fetch(String url, FetchProfile p) throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new","--disable-gpu","--no-sandbox");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get(url);
            waitReady(driver, 20);

            if (p.dynamic()) {

                ensureCollector(driver); // #oph_collected 준비

                // 드롭다운 전부 복제(포털)
                if (p.dropdowns() != null) materializeDropdowns(driver, wait, p.dropdowns());

            }

            // 최종 스냅샷
            String html = (String)((JavascriptExecutor)driver)
                .executeScript("return document.documentElement.outerHTML");
            return Jsoup.parse(html, driver.getCurrentUrl());

        } finally {
            driver.quit();
        }
    }

    /* ---------- 공통 유틸 ---------- */

    private void waitReady(WebDriver d, int sec) {
        new WebDriverWait(d, Duration.ofSeconds(sec)).until(w ->
            ((JavascriptExecutor)w).executeScript("return document.readyState").equals("complete")
        );
    }

    private void ensureCollector(WebDriver d) {
        ((JavascriptExecutor)d).executeScript("""
          if (!document.getElementById('oph_collected')) {
            const box = document.createElement('div');
            box.id='oph_collected';
            box.style.display='none';
            document.body.appendChild(box);
          }
        """);
        injectCloneWithShadow(d); // Shadow DOM까지 복제할 수 있는 헬퍼 주입
    }

    private void injectCloneWithShadow(WebDriver d) {
        ((JavascriptExecutor)d).executeScript("""
          window.__oph_cloneWithShadow = (node) => {
            // 깊은 복제 + shadowRoot 내용을 'data-oph-shadow-root'로 평면화하여 덧붙임
            const clone = node.cloneNode(true);
            const appendShadow = (host, cloneHost) => {
              if (host.shadowRoot) {
                const holder = document.createElement('div');
                holder.setAttribute('data-oph-shadow-root','true');
                for (const child of host.shadowRoot.children) {
                  holder.appendChild(child.cloneNode(true));
                }
                cloneHost.appendChild(holder);
              }
              const kids = host.children || [];
              const kidsClone = cloneHost.children || [];
              for (let i=0; i<kids.length && i<kidsClone.length; i++) {
                appendShadow(kids[i], kidsClone[i]);
              }
            };
            appendShadow(node, clone);
            return clone;
          };
        """);
    }

    private void appendClone(WebDriver d, WebElement rootToClone, String attrName, String attrValue) {
        ((JavascriptExecutor)d).executeScript("""
          (function(root, attrName, attrValue){
            if (!window.__oph_cloneWithShadow) return;
            const clone = window.__oph_cloneWithShadow(root);
            clone.setAttribute(attrName, attrValue);
            const box = document.getElementById('oph_collected');
            if (box) box.appendChild(clone);
          })(arguments[0], arguments[1], arguments[2]);
        """, rootToClone, attrName, attrValue);
    }

    /* ---------- 재료화(복제) 동작 ---------- */

    private void materializeDropdowns(WebDriver d, WebDriverWait w, List<DropdownRecipe> dds) {
        for (var r : dds) {
            var triggers = d.findElements(By.cssSelector(r.triggerSelector()));
            for (int i=0;i<triggers.size();i++) {
                try {
                    var t = triggers.get(i);
                    ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})", t);
                    t.click();
                    w.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(r.panelSelector())));
                    var panel = d.findElement(By.cssSelector(r.cloneRootSelector()));
                    appendClone(d, panel, "data-oph-dd-index", String.valueOf(i));
                    d.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                    Thread.sleep(150);
                } catch (Exception ignore) {}
            }
        }
    }
}
