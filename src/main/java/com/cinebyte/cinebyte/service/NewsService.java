package com.cinebyte.cinebyte.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    
    @Value("${gnews.api.key:}")
    private String gnewsApiKey;

    private final RestTemplate restTemplate;

    public NewsService() {
        this.restTemplate = new RestTemplate();
    }

    @Cacheable(value = "newsCache")
    @CircuitBreaker(name = "newsApi")
    public String getMovieNews() {
        if (gnewsApiKey == null || gnewsApiKey.isEmpty() || "GNEWS_API_KEY".equals(gnewsApiKey)) {
            logger.warn("GNews API Key is missing or default. Returning empty news array.");
            return "{\"articles\": []}";
        }

        try {
            logger.info("Fetching new news from GNews API");
            String url = "https://gnews.io/api/v4/search?q=cine OR pelicula OR netflix OR hbo&lang=es&max=8&apikey=" + gnewsApiKey;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.isEmpty()) {
                return response;
            }
        } catch (Exception e) {
            logger.error("Error fetching news from GNews: {}", e.getMessage());
        }
        
        return "{\"articles\": []}";
    }
}
