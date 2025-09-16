package com.ecobazaar.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration Class
 * 
 * Provides cache manager bean for Spring Boot caching functionality
 * in the EcoBazaarX backend application.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure cache manager using ConcurrentMapCacheManager
     * 
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList("settings", "users", "products"));
        return cacheManager;
    }
}
