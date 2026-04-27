-- Flyway: inicjalna migracja
-- Wersja: 1

CREATE TABLE IF NOT EXISTS example_entity (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
