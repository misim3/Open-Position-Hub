package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.jsoup.HttpStatusException;
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
                .followRedirects(false)
                .timeout(5000)
                .get();

        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 404) {
                System.err.println("404 Not Found - URL might be deleted or moved: " + url);
            } else if (statusCode == 301 || statusCode == 302) {
                System.err.println("Redirect detected - URL might have changed: " + url);
            } else {
                System.err.println("HTTP Error " + statusCode + ": " + e.getMessage());
            }
            throw e;

        } catch (SocketTimeoutException e) {
            System.err.println("Connection timed out while accessing: " + url);
            throw e;

        } catch (UnknownHostException e) {
            System.err.println("Unknown host - Possible domain change or deletion: " + url);
            throw e;

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL - Check the URL format: " + url);
            throw e;

        } catch (IOException e) {
            System.err.println("General IO Exception occurred while fetching HTML: " + e.getMessage());
            throw e;
        }
    }
}
