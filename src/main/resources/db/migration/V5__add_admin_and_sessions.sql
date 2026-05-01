-- Añadir columna de rol a profiles
ALTER TABLE profiles ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user';

-- Crear tabla para trackear sesiones y visitas
CREATE TABLE site_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    last_ping TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    duration_seconds INTEGER NOT NULL DEFAULT 0
);