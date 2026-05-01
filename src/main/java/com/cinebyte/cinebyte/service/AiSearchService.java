package com.cinebyte.cinebyte.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiSearchService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestClient restClient;
    private final TmdbService tmdbService;
    private final ObjectMapper objectMapper;

    public AiSearchService(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public Object searchByVibe(String query) {
        String prompt = """
    Eres un experto en cine y asistente de búsqueda. Tu tarea es traducir una petición en lenguaje natural a parámetros técnicos de la API de TMDB.
    
    Petición del usuario: "%s"
    
    Debes extraer:
    1. Géneros (usa los IDs de TMDB: Acción: 28, Aventura: 12, Animación: 16, Comedia: 35, Crimen: 80, Documental: 99, Drama: 18, Familia: 10751, Fantasía: 14, Historia: 36, Terror: 27, Música: 10402, Misterio: 9648, Romance: 10749, C. Ficción: 878, Suspense: 53, Bélica: 10752, Western: 37).
    2. Rango de años (primary_release_date.gte y primary_release_date.lte en formato YYYY-MM-DD).
    3. Idioma original si se menciona un país (with_original_language: ja=japonés, ko=coreano, fr=francés, it=italiano, es=español, de=alemán, zh=chino, hi=hindi, en=inglés).
    4. Orden (sort_by: popularity.desc, vote_average.desc, primary_release_date.desc).
    5. Puntuación mínima si se menciona calidad (vote_average.gte: número del 1 al 10).
    
    Responde ÚNICAMENTE con un JSON con este formato exacto, sin texto adicional ni markdown:
    {
      "with_genres": "ID1,ID2",
      "primary_release_date.gte": "YYYY-MM-DD",
      "primary_release_date.lte": "YYYY-MM-DD",
      "with_original_language": "xx",
      "sort_by": "popularity.desc",
      "vote_average.gte": "7"
    }
    Incluye solo los campos que apliquen. Omite los que no sean relevantes.
    """.formatted(query);

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            System.out.println("=== CALLING GEMINI ===");

            String response = restClient.post()
                    .uri("/v1beta/models/gemma-3-12b-it:generateContent?key=" + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            System.out.println("=== GEMINI RESPONSE ===");
            System.out.println(response);
            System.out.println("======================");

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

            System.out.println("=== EXTRACTED FILTERS ===");
            System.out.println(jsonText);
            System.out.println("=========================");

            JsonNode filters = objectMapper.readTree(jsonText);
            Object tmdbResult = tmdbService.discoverWithFilters(filters);

            System.out.println("=== TMDB RESULT OK ===");

            return tmdbResult;

        } catch (Exception e) {
            System.out.println("=== AI SEARCH ERROR ===");
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("=======================");
            return Map.of("error", "Error processing AI search");
        }

    }
}
