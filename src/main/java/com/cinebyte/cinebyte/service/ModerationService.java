package com.cinebyte.cinebyte.service;

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

    public ModerationService() {
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
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            System.out.println("=== GEMINI RAW RESPONSE ===");
            System.out.println(response);
            System.out.println("===========================");

            String jsonText = extractTextFromGeminiResponse(response);
            System.out.println("=== EXTRACTED TEXT ===");
            System.out.println(jsonText);
            System.out.println("======================");
            jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();

            if (jsonText.contains("\"approved\": false") || jsonText.contains("\"approved\":false")) {
                String reason = extractReason(jsonText);
                return Map.of("approved", false, "reason", reason);
            }
            return Map.of("approved", true);

        } catch (Exception e) {
            System.out.println("=== MODERATION ERROR ===");
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("========================");
            return Map.of("approved", true);
        }
    }

    private String extractTextFromGeminiResponse(String response) {
        int textIndex = response.indexOf("\"text\": \"");
        if (textIndex == -1) return "{\"approved\": true}";
        int start = textIndex + 9;
        int end = response.indexOf("\"", start);
        return response.substring(start, end).replace("\\n", "").replace("\\\"", "\"");
    }

    private String extractReason(String json) {
        int reasonIndex = json.indexOf("\"reason\": \"");
        if (reasonIndex == -1) reasonIndex = json.indexOf("\"reason\":\"");
        if (reasonIndex == -1) return "Contenido inapropiado detectado";
        int start = json.indexOf("\"", reasonIndex + 9) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}