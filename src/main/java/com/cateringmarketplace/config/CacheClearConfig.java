package com.cateringmarketplace.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheClearConfig {

    private final CacheManager cacheManager;

    @PostConstruct
    public void clearCache() {
        log.info("Clearing all caches on startup...");
        cacheManager.getCacheNames().forEach(cacheName -> {
            try {
                cacheManager.getCache(cacheName).clear();
                log.info("Cleared cache: {}", cacheName);
            } catch (Exception e) {
                log.error("Failed to clear cache: {}", cacheName, e);
            }
        });
    }
}
