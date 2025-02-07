package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Scraper {

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scraper scraper = new Scraper();
        String url = "https://recruit.navercorp.com/rcrt/list.do?lang=ko";

        try {
            Document doc = scraper.fetchHtml(url);
            System.out.println("success");
            System.out.println(doc.html());
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }

    }

    public Document fetchHtml(String url) throws IOException {

        try {

            return Jsoup.connect(url)
                .timeout(5000)
                .get();

        } catch (IOException e) {

            System.err.println(e.getMessage());
            throw e;

        }

    }
}
