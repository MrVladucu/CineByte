package com.cinebyte.cinebyte.controller;

import com.cinebyte.cinebyte.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkContent(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Map.of("approved", true));
        }
        return ResponseEntity.ok(moderationService.checkContent(text));
    }
}