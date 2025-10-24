package com.example.Open_Position_Hub.collector.parser.ninehire;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NinehireV1Parser implements JobParser {

    private static final String key = "나인하이어/V1";
    private static final Logger logger = LoggerFactory.getLogger(NinehireV1Parser.class);
    private static final Pattern PREFIX_BRACKET_BLOCKS =
        Pattern.compile("^(?:\\s*(?:\\[[^]]*]|\\([^)]*\\)|\\{[^}]*}|<[^>]*>))+\\s*");

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {
        return List.of();
    }
}
