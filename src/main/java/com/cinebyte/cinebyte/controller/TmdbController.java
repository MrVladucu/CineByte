package com.cinebyte.cinebyte.controller;

import com.cinebyte.cinebyte.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
public class TmdbController {

    private final TmdbService tmdbService;

    @GetMapping("/search")
    public ResponseEntity<Object> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.searchMovies(query, page));
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Object> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(tmdbService.getMovieDetails(id));
    }

    @GetMapping("/movies/popular")
    public ResponseEntity<Object> getPopularMovies(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.getPopularMovies(page));
    }

    @GetMapping("/movies/trending")
    public ResponseEntity<Object> getTrendingMovies() {
        return ResponseEntity.ok(tmdbService.getTrendingMovies());
    }

    @GetMapping("/movies/{id}/credits")
    public ResponseEntity<Object> getMovieCredits(@PathVariable Long id) {
        return ResponseEntity.ok(tmdbService.getMovieCredits(id));
    }

    @GetMapping("/movies/{id}/similar")
    public ResponseEntity<Object> getSimilarMovies(@PathVariable Long id) {
        return ResponseEntity.ok(tmdbService.getSimilarMovies(id));
    }

    @GetMapping("/genres")
    public ResponseEntity<Object> getMovieGenres() {
        return ResponseEntity.ok(tmdbService.getMovieGenres());
    }

    @GetMapping("/discover")
    public ResponseEntity<Object> discoverMovies(
            @RequestParam(required = false) String genreId,
            @RequestParam(defaultValue = "popularity.desc") String sortBy,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.discoverMovies(genreId, sortBy, page));
    }
}