package com.example.Open_Position_Hub.collector.platform;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.detect.DefaultDetectorRegistry;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.collector.parser.ParserRegistry;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.util.List;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GreetingStrategy implements PlatformStrategy {

    private static final String key = "그리팅";
    private static final Logger logger = LoggerFactory.getLogger(GreetingStrategy.class);
    private final DefaultDetectorRegistry defaultDetectorRegistry;
    private final ParserRegistry parserRegistry;

    public GreetingStrategy(DefaultDetectorRegistry defaultDetectorRegistry,
        ParserRegistry parserRegistry) {
        this.defaultDetectorRegistry = defaultDetectorRegistry;
        this.parserRegistry = parserRegistry;
    }

    @Override
    public boolean supports(String platform) {
        return platform.equals(key);
    }

    @Override
    public List<JobPostingDto> scrape(Document doc, CompanyEntity company) {

        String layoutKey = defaultDetectorRegistry.detect(platformKey(), doc);

        if (layoutKey == null) {
            logger.warn("Not matched proper detector for company : {} ", company.getName());
            return null;
        }

        JobParser jobParser = parserRegistry.get(layoutKey);

        if (jobParser == null) {
            return null;
        }

        return jobParser.parse(doc, company);
    }

    @Override
    public String platformKey() {
        return key;
    }
}
