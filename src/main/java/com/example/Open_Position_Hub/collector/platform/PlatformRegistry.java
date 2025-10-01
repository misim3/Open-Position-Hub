package com.example.Open_Position_Hub.collector.platform;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlatformRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PlatformRegistry.class);
    private final List<PlatformStrategy> strategies;

    public PlatformRegistry(List<PlatformStrategy> strategies) {
        this.strategies = strategies;
    }

    public PlatformStrategy getStrategy(String platform) {
        Optional<PlatformStrategy> strategy = strategies.stream()
            .filter(s -> s.supports(platform))
            .findFirst();

        if (strategy.isPresent()) {
            return strategy.get();
        }

        logger.error("Not found Strategy for platform {} in PlatformRegistry.getStrategy",
            platform);
        return null;
    }

    public List<PlatformStrategy> getStrategies() {
        return strategies;
    }
}
