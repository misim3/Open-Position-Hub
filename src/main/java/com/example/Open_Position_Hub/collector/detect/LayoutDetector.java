package com.example.Open_Position_Hub.collector.detect;

public interface LayoutDetector {

    String platformKey();

    int order();

    String detect(DetectorDto dto);

}
