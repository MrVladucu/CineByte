package com.cinebyte.cinebyte.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TmdbService {

    private final RestClient tmdbRestClient;

    public TmdbService(@Qualifier("tmdbRestClient") RestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    public Object searchMovies(String query, int page) {
        return tmdbRestClient.get()
                .uri("/search/movie?query={query}&page={page}&language=es-ES", query, page)
                .retrieve()
                .body(Object.class);
    }

    public Object getMovieDetails(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}?language=es-ES", movieId)
                .retrieve()
                .body(Object.class);
    }

    public Object getPopularMovies(int page) {
        return tmdbRestClient.get()
                .uri("/movie/popular?page={page}&language=es-ES", page)
                .retrieve()
                .body(Object.class);
    }

    public Object getTrendingMovies() {
        return tmdbRestClient.get()
                .uri("/trending/movie/week?language=es-ES")
                .retrieve()
                .body(Object.class);
    }

    public Object getMovieCredits(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}/credits?language=es-ES", movieId)
                .retrieve()
                .body(Object.class);
    }

    public Object getSimilarMovies(Long movieId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}/similar?language=es-ES", movieId)
                .retrieve()
                .body(Object.class);
    }

    public Object getMovieGenres() {
        return tmdbRestClient.get()
                .uri("/genre/movie/list?language=es-ES")
                .retrieve()
                .body(Object.class);
    }

    public Object discoverMovies(String genreId, String sortBy, int page) {
        return tmdbRestClient.get()
                .uri("/discover/movie?with_genres={genre}&sort_by={sort}&page={page}&language=es-ES",
                        genreId, sortBy, page)
                .retrieve()
                .body(Object.class);
    }
}