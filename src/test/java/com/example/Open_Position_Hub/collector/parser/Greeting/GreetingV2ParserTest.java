package com.example.Open_Position_Hub.collector.parser.Greeting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("handleFilterBar를 스텁하고 카드 파싱을 검증한다")
    void parse_parsesCardsWithStubbedFilterBar() {
        GreetingV2Parser real = new GreetingV2Parser();
        GreetingV2Parser parser = Mockito.spy(real);

        // 필터바 스텁: 파서가 텍스트→필드 매핑을 만들 수 있도록 카테고리 제공
        Map<String, List<String>> stubOptions = Map.of(
            "직군", List.of("백엔드"),
            "경력사항", List.of("신입", "3년 이상"),
            "고용형태", List.of("정규직"),
            "근무지", List.of("서울")
        );
        doReturn(stubOptions).when(parser).handleFilterBar(anyString());

        String html = """
        <div class="sc-9b56f69e-0 enoHnQ">
          <a href="https://example.com/j3">
            <span class="sc-86b147bc-0 gIOkaZ sc-d200d649-1 dKCwbm">플랫폼 백엔드</span>
            <span class="sc-be6466ed-3 bDOHei">백엔드</span>
            <span class="sc-be6466ed-3 bDOHei">신입</span>
            <span class="sc-be6466ed-3 bDOHei">정규직</span>
            <span class="sc-be6466ed-3 bDOHei">서울</span>
          </a>
        </div>
        """;

        Document doc = Jsoup.parse(html);
        CompanyEntity company = mock(CompanyEntity.class);
        when(company.getRecruitmentUrl()).thenReturn("https://greeting.example/jobs");
        when(company.getId()).thenReturn(7L);

        List<JobPostingEntity> result = parser.parse(doc, company);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("카드가 없으면 빈 리스트")
    void parse_returnsEmptyWhenNoCards() {
        GreetingV2Parser parser = Mockito.spy(new GreetingV2Parser());
        doReturn(Map.of()).when(parser).handleFilterBar(anyString());

        Document doc = Jsoup.parse("<div id='empty'></div>");
        CompanyEntity company = mock(CompanyEntity.class);
        List<JobPostingEntity> result = parser.parse(doc, company);
        assertTrue(result.isEmpty());
    }

}
