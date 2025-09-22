package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Fetcher {

    public Document fetchHtml(String url) throws IOException {

        return Jsoup.connect(url)
            .followRedirects(false)
            .timeout(5000)
            .get();
    }
}
