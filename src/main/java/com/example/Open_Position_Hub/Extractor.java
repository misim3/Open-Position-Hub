package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {

    public void extractCompanyN(Document doc) {

        Elements jobCards = doc.select("li.card_item");

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select("h4.card_title").text();
            Elements details = card.select("dl.card_info dd.info_text");

            System.out.println("Title: " + title);
            for (Element detail : details) {
                System.out.println("Detail: " + detail.text());
            }

        }

    }

    public void extractCompanyD(Document doc) {

        Elements jobCards = doc.select("div.sc-9b56f69e-0.jlntFl");

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select("span.sc-86b147bc-0.gIOkaZ.sc-d200d649-1.dKCwbm").text();
            Elements details = card.select("span.sc-be6466ed-3.bDOHei");

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
//        String urlCompanyN = "https://recruit.navercorp.com/rcrt/list.do?lang=ko";

        String urlCompanyD = "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30";
//        String urlCompanyL = "https://team.alwayz.co/apply";
//        String urlCompanyM = "https://miridih.career.greetinghr.com/home#c4d883bb-d8e8-49bb-809b-3e818ac4e286";
//        String urlCompanyR = "https://careers.riiid.com/applynow#5089e105-e7df-45e0-9802-5509c0db0b0c";
//        String urlCompanyDD = "https://teamdoeat.career.greetinghr.com/home#323ea93b-ce52-45c9-bbbf-0b85ad135508";
        Extractor extractor = new Extractor();

        try {
            Document doc = scraper.fetchHtml(urlCompanyD);
            extractor.extractCompanyD(doc);
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }
    }
}
