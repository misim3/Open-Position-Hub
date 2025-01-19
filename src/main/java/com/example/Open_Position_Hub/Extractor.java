package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {

    private CssSelector selector;

    public Extractor(CssSelector selector) {
        this.selector = selector;
    }

    public void extractCompanyN(Document doc) {

        String company = "CompanyN";

        Map<String, String> htmlSelector = selector.getCssQuery(company);

        Elements jobCards = doc.select(htmlSelector.get("job_cards"));

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select(htmlSelector.get("job_title")).text();
            Elements details = card.select(htmlSelector.get("details"));

            System.out.println("Title: " + title);
            for (Element detail : details) {
                System.out.println("Detail: " + detail.text());
            }

            System.out.println("---------------------------------");
        }

    }

    public void extractCompanyD(Document doc) {

        String company = "CompanyD";

        Map<String, String> htmlSelector = selector.getCssQuery(company);

        Elements jobCards = doc.select(htmlSelector.get("job_cards"));

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select(htmlSelector.get("job_title")).text();
            Elements details = card.select(htmlSelector.get("details"));

            String[] detailLabels = {"직군", "경력사항", "고용형태", "근무지"};
            String[] detailValues = new String[detailLabels.length];

            for (int i = 0; i < detailValues.length; i++) {
                if (i < details.size()) {
                    detailValues[i] = details.get(i).text();
                } else {
                    detailValues[i] = "정보 없음";
                }
            }

            System.out.println("---------------------------------");
            System.out.println("직무: " + title);
            for (int i = 0; i < detailLabels.length; i++) {
                System.out.println(detailLabels[i] + ": " + detailValues[i]);
            }

        }

    }

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scraper scraper = new Scraper();
        String urlCompanyN = "https://recruit.navercorp.com/rcrt/list.do?lang=ko";
        String urlCompanyD = "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30";

        Extractor extractor = new Extractor(new CssSelector());

        try {
            Document doc = scraper.fetchHtml(urlCompanyN);
            extractor.extractCompanyN(doc);
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }
    }
}
