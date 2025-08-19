package com.example.Open_Position_Hub.collector.parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ParserRegistry {

    private final Map<String, JobParser> byKey;


    public ParserRegistry(List<JobParser> parsers) {
        this.byKey = parsers.stream()
            .collect(Collectors.toUnmodifiableMap(
                JobParser::layoutKey,
                p -> p,
                (a,b) -> {
                    throw new  IllegalStateException("Duplicate key: " + a.layoutKey());
                }
            ));
    }

    public JobParser get(String layoutKey) {
        JobParser p = byKey.get(layoutKey);
        if (p == null) {
            throw new IllegalArgumentException("No parser for layout key: " + layoutKey);
        }
        return p;
    }
}
