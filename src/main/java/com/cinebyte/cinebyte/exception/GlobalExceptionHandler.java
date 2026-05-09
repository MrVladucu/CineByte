package com.cinebyte.cinebyte.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de rutas no encontradas (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Ruta no encontrada", ex.getMessage());
    }

    // Maneja errores provenientes de APIs externas (como TMDB devolviendo 401 o 404)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientError(HttpClientErrorException ex) {
        return buildErrorResponse((HttpStatus) ex.getStatusCode(), "Error en el cliente de la API externa", ex.getMessage());
    }

    // Maneja fallos en servidores externos (ej. TMDB caído)
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpServerError(HttpServerErrorException ex) {
        return buildErrorResponse((HttpStatus) ex.getStatusCode(), "Error en el servidor de la API externa", ex.getMessage());
    }

    // Maneja cuando el Circuit Breaker de Resilience4j está abierto (corta el circuito para evitar saturación)
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Servicio temporalmente no disponible", "El proveedor externo está fallando. Se ha cortado el circuito para proteger el sistema.");
    }

    // Manejador global por defecto (para cualquier otro error inesperado 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
