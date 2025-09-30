package com.example.Open_Position_Hub.collector.detect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingV2DetectorTest {

    @Test
    @DisplayName("platformKey는 '그리팅', order는 1")
    void meta_info() {
        GreetingV2Detector d = new GreetingV2Detector();
        assertEquals("그리팅", d.platformKey());
        assertEquals(1, d.order());
    }

    @Test
    @DisplayName("div[listviewtype='a']가 있으면 '그리팅/V2'을 반환")
    void detect_hitsWhenListViewTypeA() {
        GreetingV2Detector d = new GreetingV2Detector();
        Document doc = Jsoup.parse("<div listviewtype='a'></div>");
        String hit = d.detect(doc);
        assertNotNull(hit);
        assertEquals("그리팅/V2", hit);
    }

    @Test
    @DisplayName("해당 요소가 없으면 null")
    void detect_returnNullWhenNoMatch() {
        GreetingV2Detector d = new GreetingV2Detector();
        Document doc = Jsoup.parse("<div listviewtype='b'></div>");
        assertNull(d.detect(doc));
    }
}
