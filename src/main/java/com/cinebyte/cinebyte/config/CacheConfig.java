package com.cinebyte.cinebyte.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Caché general para las búsquedas a TMDB (1 hora de duración, tamaño máximo de 500 para proteger memoria)
        cacheManager.registerCustomCache("tmdbCache", Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(500)
                .build());
                
        // Caché especial para las noticias de GNews (6 horas de duración, un solo registro esencial)
        cacheManager.registerCustomCache("newsCache", Caffeine.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .maximumSize(10)
                .build());

        return cacheManager;
    }
}
