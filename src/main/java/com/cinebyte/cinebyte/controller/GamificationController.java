package com.cinebyte.cinebyte.controller;

import com.cinebyte.cinebyte.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @PostMapping("/review")
    public ResponseEntity<Map<String, Object>> processReview(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        return ResponseEntity.ok(gamificationService.processReview(userId));
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        return ResponseEntity.ok(gamificationService.getUserStats(UUID.fromString(userId)));
    }

    @GetMapping("/achievements/{userId}")
    public ResponseEntity<Object> getUserAchievements(@PathVariable String userId) {
        return ResponseEntity.ok(gamificationService.getUserAchievements(UUID.fromString(userId)));
    }
}