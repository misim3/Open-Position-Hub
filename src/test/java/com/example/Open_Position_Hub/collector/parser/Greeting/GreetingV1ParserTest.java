package com.example.Open_Position_Hub.collector.parser.Greeting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingV1ParserTest {

    @Test
    @DisplayName("layoutKey는 '그리팅/V1'")
    void layoutKey_meta() {
        GreetingV1Parser parser = new GreetingV1Parser();
        assertEquals("그리팅/V1", parser.layoutKey());
    }

    @Test
    @DisplayName("사이드바 옵션과 카드가 매칭되면 공고를 파싱한다")
    void parse_parsesCardsWithSidebarOptions() {
        GreetingV1Parser parser = new GreetingV1Parser();

        // 사이드바(카테고리별 체크박스) + 카드 2개
        String html = """
        <div class="sc-4384c63b-0 dpoYEo">
          <div class="sc-f960cb4f-0 fyUmrl">
            <span class="sc-86b147bc-0 jrtDxx">직군</span>
            <label role="checkbox">백엔드</label>
          </div>
          <div class="sc-f960cb4f-0 fyUmrl">
            <span class="sc-86b147bc-0 jrtDxx">경력사항</span>
            <label role="checkbox">신입</label>
            <label role="checkbox">3년 이상</label>
          </div>
          <div class="sc-f960cb4f-0 fyUmrl">
            <span class="sc-86b147bc-0 jrtDxx">고용형태</span>
            <label role="checkbox">정규직</label>
          </div>
          <div class="sc-f960cb4f-0 fyUmrl">
            <span class="sc-86b147bc-0 jrtDxx">근무지</span>
            <label role="checkbox">서울</label>
          </div>
        </div>

        <div class="sc-9b56f69e-0 enoHnQ">
          <a href="https://example.com/j1">
            <span class="sc-86b147bc-0 gIOkaZ sc-d200d649-1 dKCwbm">백엔드 개발자</span>
            <span class="sc-be6466ed-3 bDOHei">백엔드</span>
            <span class="sc-be6466ed-3 bDOHei">신입</span>
            <span class="sc-be6466ed-3 bDOHei">정규직</span>
            <span class="sc-be6466ed-3 bDOHei">서울</span>
          </a>
          <a href="https://example.com/j2">
            <span class="sc-86b147bc-0 gIOkaZ sc-d200d649-1 dKCwbm">서버 개발자</span>
            <span class="sc-be6466ed-3 bDOHei">백엔드</span>
            <span class="sc-be6466ed-3 bDOHei">3년 이상</span>
            <span class="sc-be6466ed-3 bDOHei">정규직</span>
            <span class="sc-be6466ed-3 bDOHei">서울</span>
          </a>
        </div>
        """;

        Document doc = Jsoup.parse(html);
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getId()).thenReturn(42L);

        List<JobPostingEntity> result = parser.parse(doc, company);

        assertNotNull(result);
        assertEquals(2, result.size(), "a 태그 2개 → 공고 2건");
    }

    @Test
    @DisplayName("카드가 없으면 빈 리스트")
    void parse_returnsEmptyWhenNoCards() {
        GreetingV1Parser parser = new GreetingV1Parser();
        Document doc = Jsoup.parse("<div id='empty'></div>");
        CompanyEntity company = mock(CompanyEntity.class);
        List<JobPostingEntity> result = parser.parse(doc, company);
        assertTrue(result.isEmpty());
    }
}
