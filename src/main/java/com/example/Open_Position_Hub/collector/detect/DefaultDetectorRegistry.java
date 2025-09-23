package com.example.Open_Position_Hub.collector.detect;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class DefaultDetectorRegistry implements DetectorRegistry {

    private final Map<String, List<LayoutDetector>> byPlatform;

    public DefaultDetectorRegistry(List<LayoutDetector> detectors) {
        this.byPlatform = detectors.stream()
            .collect(Collectors.groupingBy(
                LayoutDetector::platformKey,
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    list.sort(Comparator.comparingInt(LayoutDetector::order));
                    return Collections.unmodifiableList(list);
                })
            ));
    }

    @Override
    public String detect(String platformKey, Document doc) throws NoSuchElementException {
        List<LayoutDetector> candidates = byPlatform.getOrDefault(platformKey, List.of());
        for (LayoutDetector d : candidates) {
            try {
                return d.detect(doc);
            } catch (NoSuchElementException e) {
                //
            }
        }
        return null;
    }
}
