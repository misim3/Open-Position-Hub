package com.example.Open_Position_Hub.collector.detect;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class GreetingV1Detector implements LayoutDetector {

    private static final String key = "그리팅";

    @Override
    public String platformKey() {
        return key;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String detect(Document doc) {
        Element listViewB = doc.selectFirst("div[listviewtype='b']");
        if (listViewB != null) {
            return platformKey() + "/V1";
        }
        return null;
    }
}
