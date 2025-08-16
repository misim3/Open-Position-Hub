package com.example.Open_Position_Hub.collector.detect;

import java.net.URI;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class GreetingV2Detector implements LayoutDetector {

    @Override
    public String platformKey() {
        return "그리팅";
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public Optional<String> detect(Document doc, URI pageUrl) {
        Element listViewA = doc.selectFirst("div[listviewtype='a']");
        if (listViewA != null) {
            return Optional.of("V2");
        }
        return Optional.empty();
    }
}
