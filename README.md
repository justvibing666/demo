# Demo

Aplikacja webowa zbudowana na stosie **Spring Boot + Vue.js + PostgreSQL**, gotowa do uruchomienia lokalnie oraz w kontenerach Docker. Automatyczny deployment na Raspberry Pi przez GitHub Actions.

---

## Stos technologiczny

| Warstwa     | Technologia                                          |
|-------------|------------------------------------------------------|
| Backend     | Java 21, Spring Boot 3.3, Spring Security, Maven     |
| Frontend    | Vue 3, Vite, Pinia, Vue Router, Axios                |
| Baza danych | PostgreSQL 16                                        |
| Migracje    | Flyway                                               |
| Kontenery   | Docker, Docker Compose, Nginx                        |
| CI/CD       | GitHub Actions → self-hosted runner (Raspberry Pi)   |

---

## Architektura

```
                  ┌─────────────────────────────────────┐
                  │           Docker network             │
  :80  ┌──────────┤   Nginx (frontend)                  │
──────►│ frontend │   serwuje pliki statyczne Vue,       │
       └──────────┤   proxy /api → backend:8080          │
                  │                  │                   │
       :8080      │       ┌──────────▼──────────┐        │
──────►(internal) │       │  Spring Boot backend│        │
                  │       └──────────┬──────────┘        │
                  │                  │ JDBC              │
                  │       ┌──────────▼──────────┐        │
                  │       │   PostgreSQL 16      │        │
                  │       └─────────────────────┘        │
                  └─────────────────────────────────────┘
```

Nginx pełni dwie role: serwuje zbudowane pliki statyczne frontendu i proxy'uje żądania pod ścieżką `/api` do kontenera backendu.

---

## Architektura

Aplikacja opiera się na klasycznej trójwarstwowej architekturze:

```
Frontend (Vue 3 + Vite)
        │  HTTP / REST
        ▼
Backend (Spring Boot)
  Controller  →  Service  →  Repository  →  PostgreSQL
        │
        ▼
  Flyway (migracje schematu)
```

**Warstwy backendu:**

| Warstwa    | Pakiet                | Odpowiedzialność                       |
|------------|-----------------------|----------------------------------------|
| Controller | `com.demo.controller` | Obsługa żądań HTTP, walidacja wejścia  |
| Service    | `com.demo.service`    | Logika biznesowa, transakcje           |
| Repository | `com.demo.repository` | Dostęp do danych (Spring Data JPA)     |
| Model      | `com.demo.model`      | Encje JPA / schemat bazy danych        |
| Config     | `com.demo.config`     | Konfiguracja Spring Security i CORS    |

**Komunikacja frontend → backend** odbywa się przez Axios (`src/api/client.js`), który dodaje token JWT z `localStorage` do każdego żądania. Stan aplikacji zarządzany jest przez Pinia store (`src/store/index.js`).

---

## Struktura projektu

```
demo/
├── backend/                  # Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/demo/
│   │   │   │   ├── config/       # Konfiguracja (Security, CORS)
│   │   │   │   ├── controller/   # REST kontrolery
│   │   │   │   ├── model/        # Encje JPA
│   │   │   │   ├── repository/   # Spring Data repozytoria
│   │   │   │   └── service/      # Logika biznesowa
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       └── db/migration/ # Skrypty Flyway
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                 # Vue 3 + Vite
│   ├── src/
│   │   ├── api/              # Klient HTTP (Axios)
│   │   ├── router/           # Vue Router
│   │   ├── store/            # Pinia store
│   │   └── views/            # Widoki (strony)
│   ├── vite.config.js
│   ├── nginx.conf
│   └── Dockerfile
├── docker-compose.yml        # Produkcja
├── docker-compose.dev.yml    # Developerska (tylko DB)
└── .github/
    └── deploy.yml            # GitHub Actions – deploy na RPi
```

---

## Wymagania

| Narzędzie      | Wersja min. | Do czego                       |
|----------------|-------------|--------------------------------|
| Java           | 21          | Uruchamianie backendu lokalnie |
| Node.js + npm  | 22          | Uruchamianie frontendu lokalnie|
| Docker Engine  | 24          | Kontenery                      |
| Docker Compose | v2 (plugin) | Orkiestracja kontenerów        |

---

## Uruchomienie

### Tryb deweloperski (hot-reload)

Uruchom bazę danych w kontenerze, backend i frontend lokalnie.

```bash
# 1. Baza danych
docker compose -f docker-compose.dev.yml up -d

# 2. Backend (profil dev, port 8080)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Frontend (port 5173)
cd frontend
npm install
npm run dev
```

| Serwis      | Adres                                           |
|-------------|-------------------------------------------------|
| Frontend    | http://localhost:5173                           |
| Backend API | http://localhost:8080/api                       |
| Health      | http://localhost:8080/api/actuator/health       |

### Produkcja (Docker Compose)

Buduje wszystkie obrazy i uruchamia pełny stos. Kolejność startowania jest wymuszona przez healthchecki:
`db` → `backend` → `frontend`.

```bash
docker compose up --build -d
```

Aplikacja dostępna pod adresem: http://localhost

Zatrzymanie i usunięcie kontenerów:

```bash
docker compose down          # zatrzymuje
docker compose down -v       # zatrzymuje + usuwa wolumen DB
```

---

## Konfiguracja

### Zmienne środowiskowe backendu

Wartości domyślne zdefiniowane w `application.yml`. Można je nadpisać zmiennymi środowiskowymi lub plikiem `.env` w katalogu głównym.

| Zmienna       | Domyślna wartość | Opis                   |
|---------------|------------------|------------------------|
| `DB_HOST`     | `localhost`      | Host bazy danych       |
| `DB_PORT`     | `5432`           | Port bazy danych       |
| `DB_NAME`     | `demo_db`        | Nazwa bazy danych      |
| `DB_USER`     | `demo_user`      | Użytkownik bazy danych |
| `DB_PASSWORD` | `demo_pass`      | Hasło do bazy danych   |
| `SERVER_PORT` | `8080`           | Port serwera           |

### Profile Spring Boot

| Profil   | Plik                  | Zastosowanie                   |
|----------|-----------------------|--------------------------------|
| domyślny | `application.yml`     | Wspólna konfiguracja           |
| `dev`    | `application-dev.yml` | Logowanie SQL, Spring DevTools |
| `prod`   | (zmienne env)         | Używany w Docker Compose       |

Aktywacja profilu:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# lub przy uruchamianiu JAR
java -jar target/demo-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Pula połączeń (HikariCP)

| Parametr             | Wartość      | Opis                                              |
|----------------------|--------------|---------------------------------------------------|
| `maximum-pool-size`  | 10           | Maksymalna liczba połączeń w puli                 |
| `minimum-idle`       | 2            | Minimalna liczba bezczynnych połączeń             |
| `connection-timeout` | 30 000 ms    | Czas oczekiwania na połączenie z puli             |
| `idle-timeout`       | 600 000 ms   | Czas po którym bezczynne połączenie jest zamykane |
| `max-lifetime`       | 1 800 000 ms | Maksymalny czas życia połączenia                  |

---

## Model danych

### ExampleEntity

Główna encja aplikacji mapowana na tabelę `example_entity`.

| Pole        | Typ             | Opis                                         |
|-------------|-----------------|----------------------------------------------|
| `id`        | `Long`          | Klucz główny, auto-inkrementowany            |
| `name`      | `String`        | Nazwa rekordu (wymagana, nie może być pusta) |
| `createdAt` | `LocalDateTime` | Data utworzenia (ustawiana automatycznie)    |
| `updatedAt` | `LocalDateTime` | Data ostatniej modyfikacji (auto-aktualizowana)|

### Schemat bazy danych (V1__init_schema.sql)

```sql
CREATE TABLE IF NOT EXISTS example_entity (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

---

## API

Backend wystawia REST API pod ścieżką `/api`. Wszystkie endpointy zwracają i przyjmują JSON.

| Metoda   | Endpoint                | Opis                      | Status sukcesu    |
|----------|-------------------------|---------------------------|-------------------|
| `GET`    | `/api/examples`         | Lista wszystkich rekordów | `200 OK`          |
| `GET`    | `/api/examples/{id}`    | Pobranie rekordu po ID    | `200 OK`          |
| `POST`   | `/api/examples`         | Utworzenie nowego rekordu | `201 Created`     |
| `DELETE` | `/api/examples/{id}`    | Usunięcie rekordu         | `204 No Content`  |
| `GET`    | `/api/actuator/health`  | Status aplikacji          | `200 OK`          |
| `GET`    | `/api/actuator/info`    | Informacje o aplikacji    | `200 OK`          |
| `GET`    | `/api/actuator/metrics` | Metryki aplikacji         | `200 OK`          |

### Przykłady wywołań

**Pobranie wszystkich rekordów:**
```bash
curl -X GET http://localhost:8080/api/examples
```
```json
[
  {
    "id": 1,
    "name": "Przykład",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

**Utworzenie nowego rekordu:**
```bash
curl -X POST http://localhost:8080/api/examples \
  -H "Content-Type: application/json" \
  -d '{"name": "Nowy rekord"}'
```
```json
{
  "id": 2,
  "name": "Nowy rekord",
  "createdAt": "2024-01-15T11:00:00",
  "updatedAt": "2024-01-15T11:00:00"
}
```

**Pobranie rekordu po ID:**
```bash
curl -X GET http://localhost:8080/api/examples/1
```

**Usunięcie rekordu:**
```bash
curl -X DELETE http://localhost:8080/api/examples/1
```

---

## Frontend

### Routing (Vue Router)

Aplikacja korzysta z trybu `history` (bez `#` w URL).

| Ścieżka  | Widok       | Ładowanie       |
|----------|-------------|-----------------|
| `/`      | `HomeView`  | Natychmiastowe  |
| `/about` | `AboutView` | Lazy loading    |

### Zarządzanie stanem (Pinia)

Store `useExampleStore` (`src/store/index.js`) udostępnia:

| Element           | Typ          | Opis                                        |
|-------------------|--------------|---------------------------------------------|
| `items`           | `ref([])`    | Lista pobranych rekordów                    |
| `loading`         | `ref(false)` | Flaga ładowania (do wyświetlania spinnera)  |
| `error`           | `ref(null)`  | Komunikat błędu z ostatniego żądania        |
| `fetchAll()`      | async        | Pobiera wszystkie rekordy z API             |
| `create(payload)` | async        | Tworzy nowy rekord i dodaje go do listy     |
| `remove(id)`      | async        | Usuwa rekord i odświeża lokalną listę       |

### Klient HTTP (Axios)

`src/api/client.js` konfiguruje Axios z:

- `baseURL: '/api'` – proxy przez Nginx w produkcji
- `timeout: 10 000 ms`
- **Request interceptor** – automatyczne dołączanie tokenu JWT z `localStorage`
- **Response interceptor** – globalna obsługa błędu `401 Unauthorized`

---

## Bezpieczeństwo

Backend używa Spring Security w trybie **bezstanowym** (JWT-ready):

- Sesje HTTP wyłączone (`SessionCreationPolicy.STATELESS`)
- CSRF wyłączony (API bezstanowe)
- CORS skonfigurowany dla deweloperskich portów lokalnych

### Konfiguracja CORS

| Parametr          | Wartość                                          |
|-------------------|--------------------------------------------------|
| Dozwolone originy | `http://localhost:5173`, `http://localhost:3000` |
| Dozwolone metody  | GET, POST, PUT, PATCH, DELETE, OPTIONS           |
| Dozwolone nagłówki| `*` (wszystkie)                                  |
| Credentials       | Tak                                              |

> Przed wdrożeniem produkcyjnym należy ograniczyć `allowedOrigins` do właściwej domeny.

---

## Baza danych

### Schemat

Tabela `example_entity` tworzona przez Flyway przy pierwszym uruchomieniu:

```sql
CREATE TABLE IF NOT EXISTS example_entity (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

### Migracje (Flyway)

Flyway uruchamia migracje automatycznie przy starcie aplikacji. Parametr `baseline-on-migrate: true` pozwala na migrację istniejącej bazy bez historii Flyway.

Konwencja: `V{numer}__{opis}.sql`, np. `V1__init_schema.sql`, `V2__add_description_column.sql`.

Flyway uruchamia migracje automatycznie przy starcie aplikacji. Historię migracji przechowuje w tabeli `flyway_schema_history`.

Ręczne wykonanie migracji (bez startu aplikacji):

```bash
cd backend
./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/demo_db \
  -Dflyway.user=demo_user -Dflyway.password=demo_pass
```

---

## CI/CD – Deployment na Raspberry Pi

Każdy push na branch `main` uruchamia workflow `.github/deploy.yml` na self-hosted runnerze zainstalowanym na Raspberry Pi.

### Kroki pipeline

```
push → main
  └── deploy (self-hosted runner na RPi)
        ├── checkout kodu
        ├── docker compose down --remove-orphans
        ├── git pull origin main
        ├── docker compose up --build -d
        └── docker compose ps (weryfikacja)
```

### Konfiguracja runnera na RPi

1. W repozytorium GitHub: **Settings → Actions → Runners → New self-hosted runner**
2. Postępuj zgodnie z instrukcjami instalacji dla Linux (ARM)
3. Runner musi mieć dostęp do Dockera:
   ```bash
   sudo usermod -aG docker $USER   # dodaj usera runnera do grupy docker
   ```
4. Projekt powinien być sklonowany w `/opt/demo` na Raspberry Pi.

---

## Budowanie

### Backend (plik JAR)

```bash
cd backend
./mvnw package -DskipTests
# Plik wynikowy: target/demo-backend-0.0.1-SNAPSHOT.jar
```

### Frontend (pliki statyczne)

```bash
cd frontend
npm run build
# Pliki wynikowe: dist/
```

### Obrazy Docker

```bash
# Backend
docker build -t demo-backend ./backend

# Frontend
docker build -t demo-frontend ./frontend
```

---

## Testy

```bash
cd backend
./mvnw test
```

Projekt zawiera:
- `DemoApplicationTests` – test kontekstu Spring (smoke test)
- Zależność `spring-security-test` dla testów wymagających kontekstu bezpieczeństwa

---

## Porady deweloperskie

**Resetowanie bazy danych w trybie dev:**
```bash
docker compose -f docker-compose.dev.yml down -v
docker compose -f docker-compose.dev.yml up -d
```

**Podgląd logów backendu w Docker:**
```bash
docker logs -f demo-backend
```

**Podgląd logów SQL (profil dev):**
Logi zapytań SQL są automatycznie włączone w profilu `dev` – widoczne w konsoli podczas uruchamiania `./mvnw spring-boot:run`.

**Linting frontendu:**
```bash
cd frontend
npm run lint
```

Docker Compose używa tego endpointu jako healthcheck backendu (sprawdzany co 30s, 3 próby).

---

## Logowanie

| Logger     | Poziom  |
|------------|---------|
| `root`     | `INFO`  |
| `com.demo` | `DEBUG` |

Format logu: `{data} [{wątek}] {poziom} {logger} - {wiadomość}`

W profilu `dev` włączone jest dodatkowo logowanie zapytań SQL (`show-sql: true`).
