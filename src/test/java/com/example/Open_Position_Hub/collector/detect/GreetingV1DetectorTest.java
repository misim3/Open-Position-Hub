package com.example.Open_Position_Hub.collector.detect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingV1DetectorTest {

    @Test
    @DisplayName("platformKey는 '그리팅', order는 0")
    void meta_info() {
        GreetingV1Detector d = new GreetingV1Detector();
        assertEquals("그리팅", d.platformKey());
        assertEquals(0, d.order());
    }

    @Test
    @DisplayName("div[listviewtype='b']가 있으면 '그리팅/V1'을 반환")
    void detect_hitsWhenListViewTypeB() {
        GreetingV1Detector d = new GreetingV1Detector();
        Document doc = Jsoup.parse("<div listviewtype='b'></div>");
        String hit = d.detect(doc);
        assertNotNull(hit);
        assertEquals("그리팅/V1", hit);
    }

    @Test
    @DisplayName("해당 요소가 없으면 null")
    void detect_returnNullWhenNoMatch() {
        GreetingV1Detector d = new GreetingV1Detector();
        Document doc = Jsoup.parse("<div listviewtype='a'></div>");
        assertNull(d.detect(doc));
    }
}
