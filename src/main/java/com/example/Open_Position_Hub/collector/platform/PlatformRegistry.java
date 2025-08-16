package com.example.Open_Position_Hub.collector.platform;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PlatformRegistry {

    private final List<PlatformStrategy> strategies;

    public PlatformRegistry(List<PlatformStrategy> strategies) {
        this.strategies = strategies;
    }

    public PlatformStrategy getStrategy(String platform) {
        return strategies.stream()
            .filter(s -> s.supports(platform))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No strategy found for platform " + platform));
    }

    public List<PlatformStrategy> getStrategies() {
        return strategies;
    }
}
