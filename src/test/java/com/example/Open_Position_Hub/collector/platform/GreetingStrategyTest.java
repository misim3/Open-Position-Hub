package com.example.Open_Position_Hub.collector.platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.detect.DefaultDetectorRegistry;
import com.example.Open_Position_Hub.collector.parser.ParserRegistry;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingStrategyTest {

    @Test
    @DisplayName("platformKey는 '그리팅'을 반환한다")
    void platformKey_returnsGreeting() {
        DefaultDetectorRegistry detector = mock(DefaultDetectorRegistry.class);
        ParserRegistry parserRegistry = mock(ParserRegistry.class);

        GreetingStrategy strategy = new GreetingStrategy(detector, parserRegistry);

        assertEquals("그리팅", strategy.platformKey());
    }

    @Test
    @DisplayName("supports는 platformKey와 일치할 때만 true를 반환한다(일반 구현 가정)")
    void supports_matchesPlatformKey() {
        DefaultDetectorRegistry detector = mock(DefaultDetectorRegistry.class);
        ParserRegistry parserRegistry = mock(ParserRegistry.class);
        GreetingStrategy strategy = new GreetingStrategy(detector, parserRegistry);

        assertTrue(strategy.supports("그리팅"));
        assertFalse(strategy.supports("원티드"));
        assertFalse(strategy.supports("greeting")); // 대소문자/영문 미일치 예시
    }

    @Test
    @DisplayName("레이아웃 미검출 시 scrape는 빈 리스트를 반환한다")
    void scrape_returnsEmptyList_whenNoLayoutDetected() {
        DefaultDetectorRegistry detector = mock(DefaultDetectorRegistry.class);
        ParserRegistry parserRegistry = mock(ParserRegistry.class);

        GreetingStrategy strategy = new GreetingStrategy(detector, parserRegistry);

        Document doc = mock(Document.class);
        CompanyEntity company = mock(CompanyEntity.class);

        when(detector.detect(strategy.platformKey(), doc)).thenReturn(Optional.empty());

        List<JobPostingDto> result = strategy.scrape(doc, company);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
