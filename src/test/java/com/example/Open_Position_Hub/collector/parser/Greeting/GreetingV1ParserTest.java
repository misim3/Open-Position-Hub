package com.example.Open_Position_Hub.collector.parser.Greeting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.db.CompanyEntity;
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
    @DisplayName("사이드바 옵션과 카드가 매칭되면 공고를 파싱한다 (ul a[href] 구조 대응)")
    void parse_parsesCardsWithSidebarOptions() {
        GreetingV1Parser parser = new GreetingV1Parser();

        // 파서의 셀렉터와 일치하는 HTML 구성
        // - 사이드바: div.sc-9b56f69e-0.imkSIw.sc-9b6acf96-0.mgFVD > div.sc-c7f48e72-0.biJzyB
        //   이름: span.sc-86b147bc-0.jrtDxx
        //   값: label[role='checkbox'] > span.sc-86b147bc-0.cvrGje
        // - 카드 컨테이너: div.sc-9b56f69e-0.enoHnQ
        //   링크: ul a[href]
        //   제목: span.sc-86b147bc-0.gIOkaZ.sc-f484a550-1.gMeHeg
        //   상세: span.sc-86b147bc-0.bugutw.sc-708ae078-1.gAEjfw > span.sc-708ae078-3.hBUoLe
        String html = """
        <div class="sc-9b56f69e-0 imkSIw sc-9b6acf96-0 mgFVD">
          <div class="sc-c7f48e72-0 biJzyB">
            <span class="sc-86b147bc-0 jrtDxx">직군</span>
            <label role="checkbox"><span class="sc-86b147bc-0 cvrGje">백엔드</span></label>
          </div>
          <div class="sc-c7f48e72-0 biJzyB">
            <span class="sc-86b147bc-0 jrtDxx">경력사항</span>
            <label role="checkbox"><span class="sc-86b147bc-0 cvrGje">신입</span></label>
            <label role="checkbox"><span class="sc-86b147bc-0 cvrGje">3년 이상</span></label>
          </div>
          <div class="sc-c7f48e72-0 biJzyB">
            <span class="sc-86b147bc-0 jrtDxx">고용형태</span>
            <label role="checkbox"><span class="sc-86b147bc-0 cvrGje">정규직</span></label>
          </div>
          <div class="sc-c7f48e72-0 biJzyB">
            <span class="sc-86b147bc-0 jrtDxx">근무지</span>
            <label role="checkbox"><span class="sc-86b147bc-0 cvrGje">서울</span></label>
          </div>
        </div>

        <div class="sc-9b56f69e-0 enoHnQ">
          <ul>
            <li>
              <a href="https://example.com/j1">
                <span class="sc-86b147bc-0 gIOkaZ sc-f484a550-1 gMeHeg">백엔드 개발자</span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">백엔드</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">신입</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">정규직</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">서울</span></span>
              </a>
            </li>
            <li>
              <a href="https://example.com/j2">
                <span class="sc-86b147bc-0 gIOkaZ sc-f484a550-1 gMeHeg">서버 개발자</span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">백엔드</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">3년 이상</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">정규직</span></span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw"><span class="sc-708ae078-3 hBUoLe">서울</span></span>
              </a>
            </li>
          </ul>
        </div>
        """;

        Document doc = Jsoup.parse(html);
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getId()).thenReturn(42L);
        when(company.getName()).thenReturn("ExampleCo");
        when(company.getRecruitmentUrl()).thenReturn("https://greeting.example/jobs");

        List<JobPostingDto> result = parser.parse(doc, company);

        assertNotNull(result);
        assertEquals(2, result.size(), "ul a[href] 구조에서 2건이 파싱되어야 한다");
        // 필요 시 추가 검증:
        // assertEquals("백엔드 개발자", result.get(0).getTitle());
        // assertEquals("https://example.com/j1", result.get(0).getUrl());
    }

    @Test
    @DisplayName("카드가 없으면 빈 리스트")
    void parse_returnsEmptyWhenNoCards() {
        GreetingV1Parser parser = new GreetingV1Parser();
        Document doc = Jsoup.parse("<div id='empty'></div>");
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getName()).thenReturn("ExampleCo");
        when(company.getRecruitmentUrl()).thenReturn("https://greeting.example/jobs");

        List<JobPostingDto> result = parser.parse(doc, company);
        assertTrue(result.isEmpty());
    }
}
