package com.example.Open_Position_Hub.collector.detect;

import org.jsoup.nodes.Document;

public interface LayoutDetector {

    String platformKey();

    int order();

    String detect(Document doc);

}
