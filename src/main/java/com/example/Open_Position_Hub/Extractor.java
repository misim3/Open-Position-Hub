package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {

    public void extract(Document doc) {

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

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scraper scraper = new Scraper();
        String url = "https://recruit.navercorp.com/rcrt/list.do?lang=ko";
        Extractor extractor = new Extractor();

        try {
            Document doc = scraper.fetchHtml(url);
            extractor.extract(doc);
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }
    }
}
