package com.cinebyte.cinebyte.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@CircuitBreaker(name = "tmdbApi")
public class TmdbService {

    private final RestClient tmdbRestClient;

    public TmdbService(@Qualifier("tmdbRestClient") RestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    @Cacheable(value = "tmdbCache", key = "'search_' + #query + '_' + #page")
    public Object searchMovies(String query, int page) {
        return tmdbRestClient.get()
                .uri("/search/multi?query={query}&page={page}&language=es-ES", query, page)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'movie_details_' + #movieId")
    public Object getMovieDetails(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}?language=es-ES&append_to_response=videos", movieId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'popular_movies_' + #page")
    public Object getPopularMovies(int page) {
        return tmdbRestClient.get()
                .uri("/movie/popular?page={page}&language=es-ES", page)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'trending_movies'")
    public Object getTrendingMovies() {
        return tmdbRestClient.get()
                .uri("/trending/movie/week?language=es-ES")
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'movie_credits_' + #movieId")
    public Object getMovieCredits(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}/credits?language=es-ES", movieId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'movie_providers_' + #movieId")
    public Object getMovieProviders(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}/watch/providers", movieId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'similar_movies_' + #movieId")
    public Object getSimilarMovies(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}/similar?language=es-ES", movieId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'movie_genres'")
    public Object getMovieGenres() {
        return tmdbRestClient.get()
                .uri("/genre/movie/list?language=es-ES")
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'discover_' + #genreId + '_' + #sortBy + '_' + #page")
    public Object discoverMovies(String genreId, String sortBy, int page) {
        return tmdbRestClient.get()
                .uri("/discover/movie?with_genres={genre}&sort_by={sort}&page={page}&language=es-ES",
                        genreId, sortBy, page)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'discover_filters_' + #filters.hashCode()")
    public Object discoverWithFilters(com.fasterxml.jackson.databind.JsonNode filters) {
        StringBuilder uriBuilder = new StringBuilder("/discover/movie?language=es-ES");
        filters.fields().forEachRemaining(entry -> {
            if (!entry.getValue().asText().isEmpty()) {
                uriBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue().asText());
            }
        });

        return tmdbRestClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .body(Object.class);
    }

    // --- TV SHOWS ---
    @Cacheable(value = "tmdbCache", key = "'trending_tv'")
    public Object getTrendingTv() {
        return tmdbRestClient.get()
                .uri("/trending/tv/week?language=es-ES")
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'tv_details_' + #tvId")
    public Object getTvDetails(Long tvId) {
        return tmdbRestClient.get()
                .uri("/tv/{id}?language=es-ES&append_to_response=videos", tvId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'tv_credits_' + #tvId")
    public Object getTvCredits(Long tvId) {
        return tmdbRestClient.get()
                .uri("/tv/{id}/credits?language=es-ES", tvId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'tv_providers_' + #tvId")
    public Object getTvProviders(Long tvId) {
        return tmdbRestClient.get()
                .uri("/tv/{id}/watch/providers", tvId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'similar_tv_' + #tvId")
    public Object getSimilarTv(Long tvId) {
        return tmdbRestClient.get()
                .uri("/tv/{id}/similar?language=es-ES", tvId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'tv_season_' + #tvId + '_' + #seasonNumber")
    public Object getTvSeason(Long tvId, Integer seasonNumber) {
        return tmdbRestClient.get()
                .uri("/tv/{id}/season/{seasonNumber}?language=es-ES", tvId, seasonNumber)
                .retrieve()
                .body(Object.class);
    }

    // --- PERSON / ACTORS ----
    @Cacheable(value = "tmdbCache", key = "'person_details_' + #personId")
    public Object getPersonDetails(Long personId) {
        return tmdbRestClient.get()
                .uri("/person/{id}?language=es-ES&append_to_response=external_ids", personId)
                .retrieve()
                .body(Object.class);
    }

    @Cacheable(value = "tmdbCache", key = "'person_credits_' + #personId")
    public Object getPersonCombinedCredits(Long personId) {
        return tmdbRestClient.get()
                .uri("/person/{id}/combined_credits?language=es-ES", personId)
                .retrieve()
                .body(Object.class);
    }
}