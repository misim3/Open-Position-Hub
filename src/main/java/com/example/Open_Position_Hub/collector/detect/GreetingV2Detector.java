package com.example.Open_Position_Hub.collector.detect;

import java.util.NoSuchElementException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class GreetingV2Detector implements LayoutDetector {

    private static final String key = "그리팅";

    @Override
    public String platformKey() {
        return key;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public String detect(Document doc) throws NoSuchElementException {
        Element listViewA = doc.selectFirst("div[listviewtype='a']");
        if (listViewA != null) {
            return platformKey() + "/V2";
        } else {
            throw new NoSuchElementException("No such element in GreetingV1Detector");
        }
    }
}
