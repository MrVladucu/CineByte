-- USUARIOS
CREATE TABLE users (
                       id          BIGSERIAL PRIMARY KEY,
                       username    VARCHAR(50)  NOT NULL UNIQUE,
                       email       VARCHAR(100) NOT NULL UNIQUE,
                       password    VARCHAR(255) NOT NULL,
                       bio         TEXT,
                       avatar_url  VARCHAR(255),
                       created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- SEGUIDORES
CREATE TABLE follows (
                         follower_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         following_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                         PRIMARY KEY (follower_id, following_id)
);

-- RESEÑAS
CREATE TABLE reviews (
                         id            BIGSERIAL PRIMARY KEY,
                         user_id       BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         tmdb_movie_id BIGINT    NOT NULL,
                         rating        SMALLINT  NOT NULL CHECK (rating >= 1 AND rating <= 10),
                         content       TEXT,
                         created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                         UNIQUE (user_id, tmdb_movie_id)
);

-- LIKES EN RESEÑAS
CREATE TABLE review_likes (
                              user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              review_id  BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              PRIMARY KEY (user_id, review_id)
);

-- LISTAS
CREATE TABLE lists (
                       id          BIGSERIAL PRIMARY KEY,
                       user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
                               user_id    BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               content    TEXT      NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- HISTORIAL DE PELÍCULAS VISTAS
CREATE TABLE watched_movies (
                                user_id       BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                tmdb_movie_id BIGINT    NOT NULL,
                                watched_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                                PRIMARY KEY (user_id, tmdb_movie_id)
);

-- PELÍCULAS FAVORITAS
CREATE TABLE favorite_movies (
                                 user_id       BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 tmdb_movie_id BIGINT    NOT NULL,
                                 added_at      TIMESTAMP NOT NULL DEFAULT NOW(),
                                 PRIMARY KEY (user_id, tmdb_movie_id)
);