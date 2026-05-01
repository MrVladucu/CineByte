-- Agregar columna background_media_type a profiles
ALTER TABLE profiles ADD COLUMN background_media_type VARCHAR(10) NOT NULL DEFAULT 'movie';