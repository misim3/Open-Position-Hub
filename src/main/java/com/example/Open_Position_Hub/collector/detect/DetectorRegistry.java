package com.example.Open_Position_Hub.collector.detect;

import java.util.NoSuchElementException;
import org.jsoup.nodes.Document;

public interface DetectorRegistry {

    String detect(String platformKey, Document doc) throws NoSuchElementException;
}
