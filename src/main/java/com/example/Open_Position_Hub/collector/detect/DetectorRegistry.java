package com.example.Open_Position_Hub.collector.detect;

import java.net.URI;
import java.util.Optional;
import org.jsoup.nodes.Document;

public interface DetectorRegistry {

    Optional<String> detect(String platformKey, Document doc);
}
