-- USUARIOS (perfil público, complementa a Supabase Auth)
CREATE TABLE profiles (
                          id          UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
                          username    VARCHAR(50)  NOT NULL UNIQUE,
                          bio         TEXT,
                          avatar_url  VARCHAR(255),
                          created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- SEGUIDORES
CREATE TABLE follows (
                         follower_id  UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                         following_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                         created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                         PRIMARY KEY (follower_id, following_id)
);

-- RESEÑAS
CREATE TABLE reviews (
                         id            BIGSERIAL PRIMARY KEY,
                         user_id       UUID      NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                         tmdb_movie_id BIGINT    NOT NULL,
                         rating        SMALLINT  NOT NULL CHECK (rating >= 1 AND rating <= 10),
                         content       TEXT,
                         created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                         UNIQUE (user_id, tmdb_movie_id)
);

-- LIKES EN RESEÑAS
CREATE TABLE review_likes (
                              user_id    UUID   NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                              review_id  BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              PRIMARY KEY (user_id, review_id)
);

-- LISTAS
CREATE TABLE lists (
                       id          BIGSERIAL PRIMARY KEY,
                       user_id     UUID         NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                       name        VARCHAR(100) NOT NULL,
                       description TEXT,
                       is_public   BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- PELÍCULAS EN LISTAS
CREATE TABLE list_movies (
                             list_id       BIGINT NOT NULL REFERENCES lists(id) ON DELETE CASCADE,
                             tmdb_movie_id BIGINT NOT NULL,
                             added_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                             PRIMARY KEY (list_id, tmdb_movie_id)
);

-- COMENTARIOS EN LISTAS
CREATE TABLE list_comments (
                               id         BIGSERIAL PRIMARY KEY,
                               list_id    BIGINT    NOT NULL REFERENCES lists(id) ON DELETE CASCADE,
                               user_id    UUID      NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                               content    TEXT      NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- HISTORIAL DE PELÍCULAS VISTAS
CREATE TABLE watched_movies (
                                user_id       UUID   NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                tmdb_movie_id BIGINT NOT NULL,
                                watched_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                                PRIMARY KEY (user_id, tmdb_movie_id)
);

-- PELÍCULAS FAVORITAS
CREATE TABLE favorite_movies (
                                 user_id       UUID   NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                 tmdb_movie_id BIGINT NOT NULL,
                                 added_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                                 PRIMARY KEY (user_id, tmdb_movie_id)
);

-- ESTADÍSTICAS DE GAMIFICACIÓN
CREATE TABLE user_stats (
                            user_id       UUID    PRIMARY KEY REFERENCES profiles(id) ON DELETE CASCADE,
                            xp            INTEGER NOT NULL DEFAULT 0,
                            level         INTEGER NOT NULL DEFAULT 1,
                            streak        INTEGER NOT NULL DEFAULT 0,
                            last_activity DATE,
                            updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- CATÁLOGO DE TROFEOS
CREATE TABLE achievements (
                              id          BIGSERIAL PRIMARY KEY,
                              code        VARCHAR(50)  NOT NULL UNIQUE,
                              name        VARCHAR(100) NOT NULL,
                              description TEXT         NOT NULL,
                              xp_required INTEGER      NOT NULL,
                              icon        VARCHAR(50)
);

-- TROFEOS DESBLOQUEADOS POR USUARIO
CREATE TABLE user_achievements (
                                   user_id        UUID   NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                   achievement_id BIGINT NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
                                   unlocked_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                                   PRIMARY KEY (user_id, achievement_id)
);

-- TROFEOS INICIALES
INSERT INTO achievements (code, name, description, xp_required, icon) VALUES
                                                                          ('FIRST_REVIEW',   'Primera reseña',      'Escribe tu primera reseña',        0,   '🎬'),
                                                                          ('REVIEWER_10',    'Crítico en prácticas', 'Escribe 10 reseñas',              100,  '📝'),
                                                                          ('REVIEWER_50',    'Crítico profesional',  'Escribe 50 reseñas',              500,  '🏆'),
                                                                          ('REVIEWER_100',   'Maestro del cine',     'Escribe 100 reseñas',             1000, '🎖️'),
                                                                          ('STREAK_7',       'Racha semanal',        'Mantén una racha de 7 días',      200,  '🔥'),
                                                                          ('STREAK_30',      'Racha mensual',        'Mantén una racha de 30 días',     800,  '⚡');