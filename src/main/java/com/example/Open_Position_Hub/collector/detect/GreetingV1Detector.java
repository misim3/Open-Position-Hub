package com.example.Open_Position_Hub.collector.detect;

import java.net.URI;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class GreetingV1Detector implements LayoutDetector {

    @Override
    public String platformKey() {
        return "그리팅";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public Optional<String> detect(Document doc, URI pageUrl) {
        Element listViewB = doc.selectFirst("div[listviewtype='b']");
        if (listViewB != null) {
            return Optional.of("V1");
        }
        return Optional.empty();
    }
}
