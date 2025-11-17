package com.example.Open_Position_Hub.collector.detect;

public interface DetectorRegistry {

    String detect(String platformKey, DetectorDto dto);
}
