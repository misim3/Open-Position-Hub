package com.example.Open_Position_Hub;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {

    public void extract(Document doc) {

        // Extract specific data: Example for extracting titles within <h2> tags
        Elements titles = doc.select("h2");
        System.out.println("Extracted Titles:");
        for (Element title : titles) {
            System.out.println(title.text());
        }

        // Extract links
        Elements links = doc.select("a[href]");
        System.out.println("\nExtracted Links:");
        for (Element link : links) {
            System.out.println(link.attr("href") + " - " + link.text());
        }

        // Extract images
        Elements images = doc.select("img[src]");
        System.out.println("\nExtracted Images:");
        for (Element image : images) {
            System.out.println(image.attr("src"));
        }

    }

    public static void main(String[] args) {

        Scraper scraper = new Scraper();
        String url = "https://www.google.com/";
        Extractor extractor = new Extractor();

        try {
            Document doc = scraper.fetchHtml(url);
            extractor.extract(doc);
            System.out.println("success");
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }
    }
}
