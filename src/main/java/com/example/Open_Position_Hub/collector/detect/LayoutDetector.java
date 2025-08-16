package com.example.Open_Position_Hub.collector.detect;

import java.net.URI;
import java.util.Optional;
import org.jsoup.nodes.Document;

public interface LayoutDetector {

    String platformKey();
    int order();
    Optional<String> detect(Document doc, URI pageUrl);

}
