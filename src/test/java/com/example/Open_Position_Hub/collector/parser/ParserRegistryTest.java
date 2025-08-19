package com.example.Open_Position_Hub.collector.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParserRegistryTest {

    static class DummyParserA implements JobParser {
        @Override public String layoutKey() { return "그리팅/V1"; }
        @Override public List<JobPostingEntity> parse(Document doc, CompanyEntity company) { return List.of(); }
    }

    static class DummyParserB implements JobParser {
        @Override public String layoutKey() { return "그리팅/V2"; }
        @Override public List<JobPostingEntity> parse(Document doc, CompanyEntity company) { return List.of(); }
    }

    static class DuplicateKeyParser implements JobParser {
        @Override public String layoutKey() { return "그리팅/V1"; }
        @Override public List<JobPostingEntity> parse(Document doc, CompanyEntity company) { return List.of(); }
    }

    @Test
    @DisplayName("layoutKey로 파서를 조회한다")
    void get_returnsParserByKey() {
        ParserRegistry reg = new ParserRegistry(List.of(new DummyParserA(), new DummyParserB()));
        JobParser p = reg.get("그리팅/V2");
        assertNotNull(p);
        assertEquals("그리팅/V2", p.layoutKey());
    }

    @Test
    @DisplayName("중복 layoutKey가 있으면 IllegalStateException")
    void constructor_throwsOnDuplicateLayoutKey() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> new ParserRegistry(List.of(new DummyParserA(), new DuplicateKeyParser())));
        assertTrue(ex.getMessage().contains("Duplicate key"), ex.getMessage());
    }

    @Test
    @DisplayName("없는 키 조회 시 IllegalArgumentException")
    void get_throwsOnMissingKey() {
        ParserRegistry reg = new ParserRegistry(List.of(new DummyParserA()));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> reg.get("그리팅/V9"));
        assertTrue(ex.getMessage().contains("No parser for layout key"), ex.getMessage());
    }
}
