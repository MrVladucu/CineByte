package com.cinebyte.cinebyte.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ModerationService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ModerationService() {
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public Map<String, Object> checkContent(String text) {
        System.out.println("=== CHECKING CONTENT: " + text + " ===");

        String prompt = """
            Eres un moderador de contenido para una red social de cine.
            Analiza el siguiente texto y determina si contiene alguno de estos elementos:
            - Hate speech, racismo o xenofobia
            - Homofobia o transfobia
            - Insultos graves o lenguaje abusivo
            - Amenazas o incitación a la violencia
            - Contenido sexual explícito
            
            Texto a analizar: "%s"
            
            Responde ÚNICAMENTE con un JSON con este formato exacto, sin texto adicional:
            {"approved": true} si el contenido es aceptable
            {"approved": false, "reason": "motivo breve en español"} si no lo es
            """.formatted(text);

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            String response = restClient.post()
                    .uri("/v1beta/models/gemma-3-12b-it:generateContent?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            System.out.println("=== GEMINI RAW RESPONSE ===");
            System.out.println(response);
            System.out.println("===========================");

            JsonNode root = objectMapper.readTree(response);
            String jsonText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            System.out.println("=== EXTRACTED TEXT ===");
            System.out.println(jsonText);
            System.out.println("======================");

            JsonNode moderationResult = objectMapper.readTree(jsonText);
            boolean approved = moderationResult.path("approved").asBoolean(true);
            
            if (!approved) {
                String reason = moderationResult.path("reason").asText("Contenido inapropiado detectado");
                return Map.of("approved", false, "reason", reason);
            }
            return Map.of("approved", true);

        } catch (Exception e) {
            System.err.println("=== MODERATION ERROR ===");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Map.of("approved", false, "reason", "Error en el servicio de moderación. Inténtalo de nuevo.");
        }
    }
}