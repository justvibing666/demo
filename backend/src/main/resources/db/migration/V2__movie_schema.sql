-- Flyway: schemat encji filmowych
-- Wersja: 2

-- Tabele słownikowe
CREATE TABLE IF NOT EXISTS genre (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS streaming_service (
    id       BIGSERIAL    PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS tag (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Encja osoby (reżyser, scenarzysta, aktor)
CREATE TABLE IF NOT EXISTS person (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(200) NOT NULL,
    bio       TEXT,
    photo_url VARCHAR(500)
);

-- Główna encja filmowa
CREATE TABLE IF NOT EXISTS movie (
    id               BIGSERIAL    PRIMARY KEY,
    title            VARCHAR(500) NOT NULL,
    production_date  DATE,
    duration_minutes INTEGER,
    thumbnail_url    VARCHAR(500),
    description      TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Zdjęcia filmowe
CREATE TABLE IF NOT EXISTS movie_photo (
    id         BIGSERIAL    PRIMARY KEY,
    movie_id   BIGINT       NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    photo_url  VARCHAR(500) NOT NULL,
    caption    VARCHAR(500),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Komentarze
CREATE TABLE IF NOT EXISTS comment (
    id          BIGSERIAL    PRIMARY KEY,
    movie_id    BIGINT       NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    author_name VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Obsada (aktor + zagrana rola)
CREATE TABLE IF NOT EXISTS movie_cast (
    id             BIGSERIAL PRIMARY KEY,
    movie_id       BIGINT    NOT NULL REFERENCES movie(id)  ON DELETE CASCADE,
    person_id      BIGINT    NOT NULL REFERENCES person(id) ON DELETE CASCADE,
    character_name VARCHAR(200),
    billing_order  INTEGER
);

-- Tabele łącznikowe many-to-many
CREATE TABLE IF NOT EXISTS movie_genre (
    movie_id BIGINT NOT NULL REFERENCES movie(id)  ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genre(id)  ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE IF NOT EXISTS movie_director (
    movie_id  BIGINT NOT NULL REFERENCES movie(id)   ON DELETE CASCADE,
    person_id BIGINT NOT NULL REFERENCES person(id)  ON DELETE CASCADE,
    PRIMARY KEY (movie_id, person_id)
);

CREATE TABLE IF NOT EXISTS movie_writer (
    movie_id  BIGINT NOT NULL REFERENCES movie(id)   ON DELETE CASCADE,
    person_id BIGINT NOT NULL REFERENCES person(id)  ON DELETE CASCADE,
    PRIMARY KEY (movie_id, person_id)
);

CREATE TABLE IF NOT EXISTS movie_streaming_service (
    movie_id             BIGINT NOT NULL REFERENCES movie(id)             ON DELETE CASCADE,
    streaming_service_id BIGINT NOT NULL REFERENCES streaming_service(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, streaming_service_id)
);

CREATE TABLE IF NOT EXISTS movie_tag (
    movie_id BIGINT NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    tag_id   BIGINT NOT NULL REFERENCES tag(id)   ON DELETE CASCADE,
    PRIMARY KEY (movie_id, tag_id)
);

-- Dane startowe dla tabeli słownikowej genre
INSERT INTO genre (name) VALUES
    ('Akcja'),
    ('Komedia'),
    ('Dramat'),
    ('Horror'),
    ('Thriller'),
    ('Sci-Fi'),
    ('Fantasy'),
    ('Animacja'),
    ('Dokumentalny'),
    ('Romans'),
    ('Przygodowy'),
    ('Kryminalny')
ON CONFLICT (name) DO NOTHING;

-- Dane startowe dla tabeli słownikowej streaming_service
INSERT INTO streaming_service (name) VALUES
    ('Netflix'),
    ('HBO Max'),
    ('Disney+'),
    ('Amazon Prime Video'),
    ('Apple TV+'),
    ('Polsat Box Go'),
    ('Player.pl')
ON CONFLICT (name) DO NOTHING;
