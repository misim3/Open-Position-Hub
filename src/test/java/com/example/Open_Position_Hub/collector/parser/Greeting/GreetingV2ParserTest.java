package com.example.Open_Position_Hub.collector.parser.Greeting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GreetingV2ParserTest {

    @Test
    @DisplayName("layoutKey는 '그리팅/V2'")
    void layoutKey_meta() {
        GreetingV2Parser parser = new GreetingV2Parser();
        assertEquals("그리팅/V2", parser.layoutKey());
    }

    @Test
    @DisplayName("handleFilterBar를 스텁하고 카드 파싱을 검증한다 (ul a[href] 구조 대응)")
    void parse_parsesCardsWithStubbedFilterBar() {
        GreetingV2Parser real = new GreetingV2Parser();
        GreetingV2Parser parser = Mockito.spy(real);

        // 필터바 스텁: 텍스트→필드 매핑을 만들 수 있도록 카테고리 제공
        Map<String, List<String>> stubOptions = Map.of(
            "직군", List.of("백엔드"),
            "경력사항", List.of("신입", "3년 이상"),
            "고용형태", List.of("정규직"),
            "근무지", List.of("서울")
        );
        doReturn(stubOptions).when(parser).handleFilterBar(anyString());

        // 파서의 현재 셀렉터와 정확히 맞춘 HTML
        // - 컨테이너: div.sc-9b56f69e-0.enoHnQ
        // - 링크: ul a[href]
        // - 제목: span.sc-86b147bc-0.gIOkaZ.sc-f484a550-1.gMeHeg
        // - 상세: span.sc-86b147bc-0.bugutw.sc-708ae078-1.gAEjfw 내부에
        //         span.sc-708ae078-3.hBUoLe 로 실제 텍스트가 들어감
        String html = """
        <div class="sc-9b56f69e-0 enoHnQ">
          <ul>
            <li>
              <a href="https://example.com/j3">
                <span class="sc-86b147bc-0 gIOkaZ sc-f484a550-1 gMeHeg">플랫폼 백엔드</span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw">
                  <span class="sc-708ae078-3 hBUoLe">백엔드</span>
                </span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw">
                  <span class="sc-708ae078-3 hBUoLe">신입</span>
                </span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw">
                  <span class="sc-708ae078-3 hBUoLe">정규직</span>
                </span>
                <span class="sc-86b147bc-0 bugutw sc-708ae078-1 gAEjfw">
                  <span class="sc-708ae078-3 hBUoLe">서울</span>
                </span>
              </a>
            </li>
          </ul>
        </div>
        """;

        Document doc = Jsoup.parse(html);
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getRecruitmentUrl()).thenReturn("https://greeting.example/jobs");
        when(company.getId()).thenReturn(7L);
        when(company.getName()).thenReturn("ExampleCo");

        List<JobPostingDto> result = parser.parse(doc, company);

        assertNotNull(result);
        assertEquals(1, result.size(), "ul a[href] 구조에서 1건이 파싱되어야 한다");
        // 필요 시 제목/링크 등 추가 검증 (게터가 있다면):
        // assertEquals("플랫폼 백엔드", result.get(0).getTitle());
        // assertEquals("https://example.com/j3", result.get(0).getUrl());
    }

    @Test
    @DisplayName("카드가 없으면 빈 리스트")
    void parse_returnsEmptyWhenNoCards() {
        GreetingV2Parser parser = Mockito.spy(new GreetingV2Parser());
        // options 비우면 parse가 바로 빈 리스트 반환(컨테이너 selectFirst 이전에 종료)
        doReturn(Map.of()).when(parser).handleFilterBar(anyString());

        Document doc = Jsoup.parse("<div id='empty'></div>");
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getRecruitmentUrl()).thenReturn("https://greeting.example/jobs");
        when(company.getName()).thenReturn("ExampleCo");

        List<JobPostingDto> result = parser.parse(doc, company);
        assertTrue(result.isEmpty());
    }
}
