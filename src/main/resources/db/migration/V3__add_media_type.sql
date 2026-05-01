-- Agregar columna media_type a las tablas para diferenciar películas y series
ALTER TABLE favorite_movies ADD COLUMN media_type VARCHAR(10) NOT NULL DEFAULT 'movie';
ALTER TABLE list_movies ADD COLUMN media_type VARCHAR(10) NOT NULL DEFAULT 'movie';
ALTER TABLE watched_movies ADD COLUMN media_type VARCHAR(10) NOT NULL DEFAULT 'movie';
ALTER TABLE reviews ADD COLUMN media_type VARCHAR(10) NOT NULL DEFAULT 'movie';

-- Actualizar claves primarias y constraints únicas para incluir media_type
ALTER TABLE favorite_movies DROP CONSTRAINT favorite_movies_pkey;
ALTER TABLE favorite_movies ADD PRIMARY KEY (user_id, tmdb_movie_id, media_type);

ALTER TABLE list_movies DROP CONSTRAINT list_movies_pkey;
ALTER TABLE list_movies ADD PRIMARY KEY (list_id, tmdb_movie_id, media_type);

ALTER TABLE watched_movies DROP CONSTRAINT watched_movies_pkey;
ALTER TABLE watched_movies ADD PRIMARY KEY (user_id, tmdb_movie_id, media_type);

ALTER TABLE reviews DROP CONSTRAINT reviews_user_id_tmdb_movie_id_key;
ALTER TABLE reviews ADD UNIQUE (user_id, tmdb_movie_id, media_type);