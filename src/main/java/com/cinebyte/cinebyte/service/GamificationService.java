package com.cinebyte.cinebyte.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final JdbcClient jdbcClient;

    private static final int XP_PER_REVIEW = 50;

    @Transactional
    public Map<String, Object> processReview(UUID userId) {
        // Obtener o crear stats del usuario
        ensureUserStats(userId);

        // Obtener stats actuales
        Map<String, Object> stats = getUserStats(userId);
        int currentXp = ((Number) stats.get("xp")).intValue();
        int currentLevel = ((Number) stats.get("level")).intValue();
        int currentStreak = ((Number) stats.get("streak")).intValue();
        LocalDate lastActivity = stats.get("last_activity") != null
                ? ((java.sql.Date) stats.get("last_activity")).toLocalDate()
                : null;

        // Calcular nueva racha
        LocalDate today = LocalDate.now();
        int newStreak = calculateStreak(lastActivity, today, currentStreak);

        // Calcular nuevo XP
        int newXp = currentXp + XP_PER_REVIEW;

        // Calcular nuevo nivel
        int newLevel = calculateLevel(newXp);

        // Actualizar stats
        jdbcClient.sql("""
                UPDATE user_stats
                SET xp = :xp, level = :level, streak = :streak,
                    last_activity = :lastActivity, updated_at = NOW()
                WHERE user_id = :userId
                """)
                .param("xp", newXp)
                .param("level", newLevel)
                .param("streak", newStreak)
                .param("lastActivity", today)
                .param("userId", userId)
                .update();

        // Contar reseñas del usuario
        int reviewCount = countUserReviews(userId);

        // Comprobar trofeos
        List<Map<String, Object>> newAchievements = checkAchievements(userId, newXp, newStreak, reviewCount);

        return Map.of(
                "xp", newXp,
                "level", newLevel,
                "streak", newStreak,
                "xpGained", XP_PER_REVIEW,
                "leveledUp", newLevel > currentLevel,
                "newAchievements", newAchievements
        );
    }

    public Map<String, Object> getUserStats(UUID userId) {
        return jdbcClient.sql("SELECT * FROM user_stats WHERE user_id = :userId")
                .param("userId", userId)
                .query()
                .singleRow();
    }

    public List<Map<String, Object>> getUserAchievements(UUID userId) {
        return jdbcClient.sql("""
                SELECT a.*, ua.unlocked_at
                FROM achievements a
                JOIN user_achievements ua ON a.id = ua.achievement_id
                WHERE ua.user_id = :userId
                ORDER BY ua.unlocked_at DESC
                """)
                .param("userId", userId)
                .query()
                .listOfRows();
    }

    private void ensureUserStats(UUID userId) {
        jdbcClient.sql("""
                INSERT INTO user_stats (user_id)
                VALUES (:userId)
                ON CONFLICT (user_id) DO NOTHING
                """)
                .param("userId", userId)
                .update();
    }

    private int calculateStreak(LocalDate lastActivity, LocalDate today, int currentStreak) {
        if (lastActivity == null) return 1;
        if (lastActivity.equals(today)) return currentStreak;
        if (lastActivity.equals(today.minusDays(1))) return currentStreak + 1;
        return 1;
    }

    private int calculateLevel(int xp) {
        int level = 1;
        int required = 100;
        while (xp >= required) {
            level++;
            required *= 2;
        }
        return level;
    }

    private int countUserReviews(UUID userId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM reviews WHERE user_id = :userId")
                .param("userId", userId)
                .query(Integer.class)
                .single();
    }

    private List<Map<String, Object>> checkAchievements(UUID userId, int xp, int streak, int reviewCount) {
        List<String> codes = new java.util.ArrayList<>();

        if (reviewCount == 1)  codes.add("FIRST_REVIEW");
        if (reviewCount == 10) codes.add("REVIEWER_10");
        if (reviewCount == 50) codes.add("REVIEWER_50");
        if (reviewCount == 100) codes.add("REVIEWER_100");
        if (streak == 7)  codes.add("STREAK_7");
        if (streak == 30) codes.add("STREAK_30");

        List<Map<String, Object>> newAchievements = new java.util.ArrayList<>();

        for (String code : codes) {
            int inserted = jdbcClient.sql("""
                    INSERT INTO user_achievements (user_id, achievement_id)
                    SELECT :userId, id FROM achievements WHERE code = :code
                    ON CONFLICT (user_id, achievement_id) DO NOTHING
                    """)
                    .param("userId", userId)
                    .param("code", code)
                    .update();

            if (inserted > 0) {
                List<Map<String, Object>> achievement = jdbcClient
                        .sql("SELECT * FROM achievements WHERE code = :code")
                        .param("code", code)
                        .query()
                        .listOfRows();
                if (!achievement.isEmpty()) {
                    newAchievements.add(achievement.get(0));
                }
            }
        }

        return newAchievements;
    }
}