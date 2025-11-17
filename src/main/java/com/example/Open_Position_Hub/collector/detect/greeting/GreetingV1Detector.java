package com.example.Open_Position_Hub.collector.detect.greeting;

import com.example.Open_Position_Hub.collector.detect.DetectorDto;
import com.example.Open_Position_Hub.collector.detect.LayoutDetector;
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
    public String detect(DetectorDto dto) {
        Element listViewB = dto.doc().selectFirst("div[listviewtype='b']");
        if (listViewB != null) {
            return platformKey() + "/V1";
        }
        return null;
    }
}
