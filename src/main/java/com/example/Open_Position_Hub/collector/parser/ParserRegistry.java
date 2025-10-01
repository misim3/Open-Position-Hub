package com.example.Open_Position_Hub.collector.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ParserRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ParserRegistry.class);
    private final Map<String, JobParser> byKey;


    public ParserRegistry(List<JobParser> parsers) {
        Map<String, JobParser> tmp = new LinkedHashMap<>();

        for (JobParser parser : parsers) {
            JobParser prev = tmp.putIfAbsent(parser.layoutKey(), parser);
            if (prev != null) {
                logger.warn("Duplicate job parser layout key={} : keeping {} and ignoring {}",
                    parser.layoutKey(), prev.getClass().getSimpleName(),
                    parser.getClass().getSimpleName());
            }
        }

        this.byKey = Map.copyOf(tmp);
    }

    public JobParser get(String layoutKey) {
        JobParser p = byKey.get(layoutKey);
        if (p == null) {
            logger.error("No parser found for layoutKey: {}", layoutKey);
            return null;
        }
        return p;
    }
}
