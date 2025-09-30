package com.example.Open_Position_Hub.collector.platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlatformRegistryTest {

    @Test
    @DisplayName("supports가 true인 첫 번째 전략을 반환한다")
    void getStrategy_returnsFirstSupportingStrategy() {
        PlatformStrategy s1 = mock(PlatformStrategy.class);
        PlatformStrategy s2 = mock(PlatformStrategy.class);

        when(s1.supports("원티드")).thenReturn(false);
        when(s2.supports("원티드")).thenReturn(true);

        PlatformRegistry registry = new PlatformRegistry(List.of(s1, s2));

        PlatformStrategy found = registry.getStrategy("원티드");
        assertSame(s2, found);
    }

    @Test
    @DisplayName("지원하는 전략이 없으면 예외를 던진다")
    void getStrategy_returnNullWhenNoStrategyFound() {
        PlatformStrategy s1 = mock(PlatformStrategy.class);
        PlatformStrategy s2 = mock(PlatformStrategy.class);

        when(s1.supports("프로그래머스")).thenReturn(false);
        when(s2.supports("프로그래머스")).thenReturn(false);

        PlatformRegistry registry = new PlatformRegistry(List.of(s1, s2));

        PlatformStrategy platformStrategy = registry.getStrategy("프로그래머스");
        assertNull(platformStrategy);
    }

    @Test
    @DisplayName("getStrategies는 주입된 리스트를 반환한다")
    void getStrategies_returnsInjectedList() {
        PlatformStrategy s1 = mock(PlatformStrategy.class);
        PlatformStrategy s2 = mock(PlatformStrategy.class);

        PlatformRegistry registry = new PlatformRegistry(List.of(s1, s2));
        List<PlatformStrategy> strategies = registry.getStrategies();

        assertEquals(2, strategies.size());
        assertSame(s1, strategies.get(0));
        assertSame(s2, strategies.get(1));
    }
}
