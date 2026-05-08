# CineByte — Backend

> API REST de CineByte: proxy de TMDB, gamificación, moderación automática con IA, búsqueda inteligente por lenguaje natural y noticias de cine.

**Documentación Swagger:** `/swagger-ui/index.html`

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Spring Boot | 4.0.3 | Framework principal de la API REST |
| Java | 21 (LTS) | Lenguaje de programación |
| Spring Security | 6 | Configuración CORS y control de acceso |
| Spring WebMVC | 6 | Endpoints REST síncronos |
| JdbcClient | Spring 6 | Operaciones JDBC con Supabase (gamificación) |
| Flyway | 10 | Migraciones de base de datos (V1–V7) |
| Lombok | latest | Reducción de boilerplate (`@RequiredArgsConstructor`, etc.) |
| Docker | latest | Containerización para despliegue reproducible |
| Springdoc OpenAPI | 2.8.6 | Documentación Swagger UI automática |
| Google Gemma 3 (12B) | v1beta | Búsqueda por lenguaje natural y moderación de reseñas |
| GNews API | v4 | Noticias de cine en español con caché de 6 horas |

---

## Variables de entorno

```env
# Base de datos (Supabase PgBouncer)
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# TMDB
TMDB_API_KEY=Bearer eyJ...
TMDB_API_BASE_URL=https://api.themoviedb.org/3

# IA y moderación
GEMINI_API_KEY=your_gemini_api_key

# Noticias
GNEWS_API_KEY=your_gnews_api_key
```

---

## Instalación y desarrollo local

### Con Docker (recomendado)

```bash
# Construir imagen
docker build -t cinebyte-backend .

# Ejecutar contenedor
docker run -p 8080:8080 --env-file .env cinebyte-backend
```

### Con Docker Compose

```bash
docker-compose up --build
```

### Sin Docker

```bash
# Requiere Java 21 y Maven 3.9+
mvn clean package -DskipTests
java -jar target/*.jar
```

La API estará disponible en `http://localhost:8080`.

---

## Estructura del proyecto

```
src/main/java/com/cinebyte/cinebyte/
├── config/
│   ├── TmdbConfig.java              # Bean RestClient con Bearer token y URL base TMDB
│   ├── CorsConfig.java              # Políticas CORS (producción + localhost)
│   ├── SecurityConfig.java          # Spring Security sin JWT (auth vía Supabase)
│   └── OpenApiConfig.java           # Metadatos Swagger UI
├── controller/
│   ├── TmdbController.java          # /api/tmdb/** (películas, series, personas, AI search)
│   ├── GamificationController.java  # /api/gamification/**
│   ├── ModerationController.java    # /api/moderation/check
│   └── NewsController.java          # /api/news
└── service/
    ├── TmdbService.java             # Llamadas a la API de TMDB
    ├── GamificationService.java     # XP, niveles, rachas y trofeos
    ├── ModerationService.java       # Moderación de texto con Google Gemma 3
    ├── AiSearchService.java         # Traducción de lenguaje natural a filtros TMDB
    └── NewsService.java             # GNews API con caché en memoria de 6 horas

src/main/resources/db/migration/
├── V1__init.sql                     # Esquema inicial completo
├── V2__add_background_movie_id.sql  # Banner de perfil
├── V3__add_media_type.sql           # Soporte para series (media_type)
├── V4__add_background_media_type.sql
├── V5__add_admin_and_sessions.sql   # Rol admin + analítica de sesiones
├── V6__add_notifications.sql        # Sistema de notificaciones
└── V7__add_episode_ratings.sql      # Valoraciones por episodio
```

---

## Endpoints de la API

### Películas — `/api/tmdb/`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tmdb/search` | Búsqueda de películas y series por título |
| GET | `/api/tmdb/ai-search` | Búsqueda por lenguaje natural (Gemma 3) |
| GET | `/api/tmdb/movies/{id}` | Detalles completos de una película |
| GET | `/api/tmdb/movies/popular` | Películas populares paginadas |
| GET | `/api/tmdb/movies/trending` | Tendencias de la semana |
| GET | `/api/tmdb/movies/{id}/credits` | Reparto y equipo técnico |
| GET | `/api/tmdb/movies/{id}/providers` | Plataformas de streaming disponibles |
| GET | `/api/tmdb/movies/{id}/similar` | Películas similares |
| GET | `/api/tmdb/movies/genres` | Catálogo de géneros |
| GET | `/api/tmdb/movies/discover` | Exploración filtrada por género, orden y página |

### Series — `/api/tmdb/tv/`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tmdb/tv/trending` | Series en tendencia esta semana |
| GET | `/api/tmdb/tv/{id}` | Detalles completos de una serie |
| GET | `/api/tmdb/tv/{id}/credits` | Reparto y equipo de una serie |
| GET | `/api/tmdb/tv/{id}/providers` | Plataformas de streaming de una serie |
| GET | `/api/tmdb/tv/{id}/similar` | Series similares |
| GET | `/api/tmdb/tv/{id}/season/{n}` | Temporada con lista de episodios |

### Personas — `/api/tmdb/person/`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tmdb/person/{id}` | Biografía de un actor o director |
| GET | `/api/tmdb/person/{id}/combined_credits` | Filmografía completa (películas + series) |

### Gamificación — `/api/gamification/`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/gamification/review` | Procesa reseña: +50 XP, nivel, racha y trofeos. Body: `{ userId }` |
| GET | `/api/gamification/stats/{userId}` | Estadísticas del usuario (XP, nivel, racha) |
| GET | `/api/gamification/achievements/{userId}` | Trofeos desbloqueados con fecha |

### Moderación e IA — `/api/moderation/`, `/api/news/`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/moderation/check` | Analiza texto con Gemma 3. Body: `{ text }` → `{ approved, reason? }` |
| GET | `/api/news` | Noticias de cine en español (caché 6h) |

---

## Sistema de gamificación

Toda la lógica reside en `GamificationService` para evitar manipulación desde el cliente.

| Mecánica | Valor | Descripción |
|---|---|---|
| XP por reseña | +50 XP | Solo si la reseña supera la moderación IA |
| Niveles | Exponencial | Nivel 2: 100 XP, cada nivel dobla el anterior (200, 400, 800…) |
| Racha diaria | +1/día | Se reinicia si pasa un día sin actividad |
| FIRST_REVIEW | Trofeo | Primera reseña publicada |
| REVIEWER_10 | Trofeo | 10 reseñas publicadas |
| REVIEWER_50 | Trofeo | 50 reseñas publicadas |
| REVIEWER_100 | Trofeo | 100 reseñas publicadas |
| STREAK_7 | Trofeo | Racha de 7 días consecutivos |
| STREAK_30 | Trofeo | Racha de 30 días consecutivos |

### Flujo completo de una reseña

```
Usuario envía reseña
       ↓
POST /api/moderation/check  →  Gemma 3 analiza el texto
       ↓
  ¿Aprobado?
  NO → devuelve { approved: false, reason: "..." } al usuario
  SÍ → frontend inserta en Supabase
       ↓
POST /api/gamification/review
       ↓
+50 XP · recalcula nivel · actualiza racha · comprueba trofeos
       ↓
Respuesta: { xp, level, streak, levelUp, newAchievements[] }
```

---

## Búsqueda inteligente por IA (AiSearchService)

El endpoint `GET /api/tmdb/ai-search?query=...` permite búsquedas en lenguaje natural:

> *"películas de terror japonés de los 90 con alta puntuación"*

**Funcionamiento:**
1. El backend construye un prompt para Google Gemma 3 (12B) pidiendo un JSON estructurado
2. Gemma extrae los parámetros técnicos de TMDB:

| Campo extraído | Descripción |
|---|---|
| `with_genres` | IDs de géneros TMDB (ej: `27,878`) |
| `primary_release_date.gte/lte` | Rango de años en formato `YYYY-MM-DD` |
| `with_original_language` | Código ISO (`ja`, `ko`, `fr`, `en`…) |
| `sort_by` | `popularity.desc`, `vote_average.desc`, etc. |
| `vote_average.gte` | Puntuación mínima (1–10) |

3. Los parámetros se pasan a `TmdbService.discoverWithFilters()` → resultados normales de TMDB

---

## Moderación de contenido (ModerationService)

Todas las reseñas pasan por moderación antes de guardarse. Detecta:

- Hate speech, racismo o xenofobia
- Homofobia o transfobia
- Insultos graves o lenguaje abusivo
- Amenazas o incitación a la violencia
- Contenido sexual explícito

> Si el servicio de moderación falla, la reseña es **bloqueada por defecto** (fail-safe).

---

## Dockerfile

Build multi-etapa: Maven compila el JAR, JRE Alpine lo ejecuta.

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Configuración CORS

La resolución de CORS requiere que `SecurityConfig` referencie explícitamente el bean `CorsConfigurationSource` de `CorsConfig`. Sin esta referencia explícita, Spring Boot 4 ignora la configuración CORS.

```java
// SecurityConfig.java
http.cors(cors -> cors.configurationSource(corsConfigurationSource))
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

---

## Despliegue (Railway)

Railway detecta automáticamente el `Dockerfile` y construye la imagen en cada push.

- Puerto expuesto: `8080` → Railway mapea a URL pública HTTPS
- Variables de entorno configuradas en el panel de Railway
- `docker-compose.yml` disponible para desarrollo local
