package com.example.Open_Position_Hub.collector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParserRegistryTest {

    @Test
    @DisplayName("layoutKey로 파서를 조회한다")
    void get_returnsParserByKey() {
        ParserRegistry reg = new ParserRegistry(List.of(new DummyParserA(), new DummyParserB()));
        JobParser p = reg.get("그리팅/V2");
        assertNotNull(p);
        assertEquals("그리팅/V2", p.layoutKey());
    }

    @Test
    @DisplayName("중복 layoutKey가 있으면 로그 남기기")
    void constructor_writeLogsOnDuplicateLayoutKey() {

        new ParserRegistry(List.of(new DummyParserA(), new DuplicateKeyParser()));

    }

    @Test
    @DisplayName("없는 키 조회 시 null 반환")
    void get_returnNullOnMissingKey() {
        ParserRegistry reg = new ParserRegistry(List.of(new DummyParserA()));
        JobParser jobParser = reg.get("그리팅/V9");
        assertNull(jobParser);
    }

    static class DummyParserA implements JobParser {

        @Override
        public String layoutKey() {
            return "그리팅/V1";
        }

        @Override
        public List<JobPostingDto> parse(Document doc, CompanyEntity company) {
            return List.of();
        }
    }

    static class DummyParserB implements JobParser {

        @Override
        public String layoutKey() {
            return "그리팅/V2";
        }

        @Override
        public List<JobPostingDto> parse(Document doc, CompanyEntity company) {
            return List.of();
        }
    }

    static class DuplicateKeyParser implements JobParser {

        @Override
        public String layoutKey() {
            return "그리팅/V1";
        }

        @Override
        public List<JobPostingDto> parse(Document doc, CompanyEntity company) {
            return List.of();
        }
    }
}
