package com.example.Open_Position_Hub;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Scraper {

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

    public static void main(String[] args) {

        Scraper scraper = new Scraper();
        String url = "https://www.google.com/";

        try {
            Document doc = scraper.fetchHtml(url);
            System.out.println("success");
            System.out.println(doc.html());
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }

    }
}
