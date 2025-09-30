package com.example.Open_Position_Hub.collector.detect;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultDetectorRegistry implements DetectorRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDetectorRegistry.class);
    private final Map<String, List<LayoutDetector>> byPlatform;

    public DefaultDetectorRegistry(List<LayoutDetector> detectors) {
        this.byPlatform = Collections.unmodifiableMap(detectors.stream()
            .collect(Collectors.groupingBy(
                LayoutDetector::platformKey,
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    list.sort(Comparator.comparingInt(LayoutDetector::order));
                    return Collections.unmodifiableList(list);
                })
            )));
    }

    @Override
    public String detect(String platformKey, Document doc) {
        List<LayoutDetector> candidates = byPlatform.getOrDefault(platformKey, List.of());

        if (candidates.isEmpty()) {
            logger.warn("LayoutDetector not found for platform {}", platformKey);
            return null;
        }

        for (LayoutDetector d : candidates) {
            String res = d.detect(doc);
            if (res != null) {
                return res;
            }
        }
        logger.error("LayoutDetector don't detect for platform {}", platformKey);
        return null;
    }
}
