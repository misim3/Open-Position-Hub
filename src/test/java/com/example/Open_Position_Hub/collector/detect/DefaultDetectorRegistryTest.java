package com.example.Open_Position_Hub.collector.detect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultDetectorRegistryTest {

    @Test
    @DisplayName("플랫폼에 등록된 디텍터가 없으면 Optional.empty()를 반환한다")
    void detect_returnsEmpty_whenNoDetectorForPlatform() {
        DefaultDetectorRegistry registry = new DefaultDetectorRegistry(List.of(
            // 다른 플랫폼의 디텍터만 등록
            new LayoutDetector() {
                @Override public String platformKey() { return "원티드"; }
                @Override public int order() { return 0; }
                @Override public Optional<String> detect(Document doc) { return Optional.of("원티드/V1"); }
            }
        ));

        Document doc = Jsoup.parse("<html></html>");
        assertTrue(registry.detect("그리팅", doc).isEmpty());
    }

    @Test
    @DisplayName("order 순으로 탐지하여, 첫 번째로 감지된 결과를 반환한다 (그리팅 V1 우선)")
    void detect_returnsFirstMatch_byOrder() {
        // GreetingV1(order=0), GreetingV2(order=1)
        DefaultDetectorRegistry registry = new DefaultDetectorRegistry(
            List.of(new GreetingV1Detector(), new GreetingV2Detector())
        );

        // V1 셀렉터와 V2 셀렉터 모두 존재하는 문서
        Document doc = Jsoup.parse(
            "<div listviewtype='b'></div>" + // V1 히트
                "<div listviewtype='a'></div>"   // V2 히트
        );

        Optional<String> hit = registry.detect("그리팅", doc);
        assertTrue(hit.isPresent());
        assertEquals("그리팅/V1", hit.get(), "order=0인 V1이 먼저 선택되어야 함");
    }

    @Test
    @DisplayName("플랫폼이 다른 디텍터는 무시한다")
    void detect_ignoresDifferentPlatform() {
        LayoutDetector otherPlatform = new LayoutDetector() {
            @Override public String platformKey() { return "원티드"; }
            @Override public int order() { return 0; }
            @Override public Optional<String> detect(Document doc) { return Optional.of("원티드/VX"); }
        };

        DefaultDetectorRegistry registry = new DefaultDetectorRegistry(List.of(
            otherPlatform, new GreetingV2Detector()
        ));

        // 그리팅 V2만 감지 가능한 문서
        Document doc = Jsoup.parse("<div listviewtype='a'></div>");

        Optional<String> hit = registry.detect("그리팅", doc);
        assertTrue(hit.isPresent());
        assertEquals("그리팅/V2", hit.get());
    }

    @Test
    @DisplayName("디텍터 detect()가 예외를 던져도 다음 디텍터로 이어서 탐지한다")
    void detect_skipsDetectorOnException() {
        LayoutDetector faulty = new LayoutDetector() {
            @Override public String platformKey() { return "그리팅"; }
            @Override public int order() { return -1; } // 가장 먼저 호출되도록
            @Override public Optional<String> detect(Document doc) {
                throw new RuntimeException("boom");
            }
        };

        DefaultDetectorRegistry registry = new DefaultDetectorRegistry(List.of(
            faulty,
            new GreetingV2Detector() // 정상 디텍터
        ));

        Document doc = Jsoup.parse("<div listviewtype='a'></div>");
        Optional<String> hit = registry.detect("그리팅", doc);
        assertTrue(hit.isPresent(), "예외가 발생해도 다음 디텍터가 감지해야 함");
        assertEquals("그리팅/V2", hit.get());
    }

    @Test
    @DisplayName("매칭되는 것이 없으면 Optional.empty()")
    void detect_returnsEmpty_whenNoMatch() {
        DefaultDetectorRegistry registry = new DefaultDetectorRegistry(
            List.of(new GreetingV1Detector(), new GreetingV2Detector())
        );

        Document doc = Jsoup.parse("<div id='nothing'></div>");
        assertTrue(registry.detect("그리팅", doc).isEmpty());
    }
}
