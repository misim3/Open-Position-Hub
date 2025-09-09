package com.example.Open_Position_Hub.collector.platform;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.detect.DefaultDetectorRegistry;
import com.example.Open_Position_Hub.collector.parser.ParserRegistry;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class GreetingStrategy implements PlatformStrategy {

    private static final String key = "그리팅";
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

        Optional<String> layoutKey = defaultDetectorRegistry.detect(platformKey(), doc);

        if (layoutKey.isPresent()) {
            return parserRegistry.get(layoutKey.get()).parse(doc, company);
        }

        return List.of();
    }

    @Override
    public String platformKey() {
        return key;
    }
}
