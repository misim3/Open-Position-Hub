package com.example.Open_Position_Hub.collector.detect.ninehire;

import com.example.Open_Position_Hub.collector.detect.LayoutDetector;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class NinehireV1Detector implements LayoutDetector {

    private static final String key = "나인하이어";
    
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
        Element container = doc.selectFirst("div[id='homepage-layout-2871b-84f0-c21e-c787-674004ce00a']");
        if (container != null) {
            return platformKey() + "/V1";
        }
        return null;
    }
}
