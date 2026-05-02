package com.cinebyte.cinebyte.controller;

import com.cinebyte.cinebyte.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getMovieNews() {
        return ResponseEntity.ok(newsService.getMovieNews());
    }
}