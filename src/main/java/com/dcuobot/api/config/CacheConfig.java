package com.dcuobot.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * Configures in-memory (Caffeine) caches for data proxied live from the Census API, so
 * repeated requests for the same character/guild/ranking/status don't each trigger a fresh
 * upstream call. Each cache gets its own TTL and size, since how quickly the underlying data
 * goes stale differs by endpoint.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    public static final String CHARACTER_LOOKUP_CACHE = "characterLookup";
    public static final String CHARACTER_RANKING_CACHE = "characterRanking";
    public static final String GUILD_LOOKUP_CACHE = "guildLookup";
    public static final String SERVER_STATUS_CACHE = "serverStatus";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                buildCache(CHARACTER_LOOKUP_CACHE, Duration.ofMinutes(5), 500),
                buildCache(CHARACTER_RANKING_CACHE, Duration.ofMinutes(10), 100),
                buildCache(GUILD_LOOKUP_CACHE, Duration.ofMinutes(5), 500),
                buildCache(SERVER_STATUS_CACHE, Duration.ofMinutes(1), 1)
        ));
        return cacheManager;
    }

    private static CaffeineCache buildCache(String name, Duration ttl, long maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(maximumSize)
                .build());
    }
}
