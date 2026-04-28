# Demo

Aplikacja webowa zbudowana na stosie **Spring Boot + Vue.js + PostgreSQL**, gotowa do uruchomienia lokalnie oraz w kontenerach Docker.

---

## Stos technologiczny

| Warstwa    | Technologia                              |
|------------|------------------------------------------|
| Backend    | Java 21, Spring Boot 3.3, Maven          |
| Frontend   | Vue 3, Vite, Pinia, Vue Router, Axios    |
| Baza danych| PostgreSQL 16                            |
| Migracje   | Flyway                                   |
| Kontenery  | Docker, Docker Compose, Nginx            |

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
│   │   ├── components/       # Komponenty wielokrotnego użytku
│   │   ├── router/           # Vue Router
│   │   ├── store/            # Pinia store
│   │   └── views/            # Widoki (strony)
│   ├── vite.config.js
│   ├── nginx.conf
│   └── Dockerfile
├── docker-compose.yml        # Produkcja
├── docker-compose.dev.yml    # Developerska (tylko DB)
└── .gitignore
```

---

## Wymagania

- **Java 21+**
- **Node.js 22+** i npm
- **Docker** i **Docker Compose** (do uruchamiania w kontenerach)
- **PostgreSQL 16** (opcjonalnie lokalnie, jeśli nie używasz Dockera)

---

## Uruchomienie

### Tryb deweloperski

Uruchom bazę danych w kontenerze, backend i frontend lokalnie (hot-reload).

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

Aplikacja dostępna pod adresem: [http://localhost:5173](http://localhost:5173)  
Backend API: [http://localhost:8080/api](http://localhost:8080/api)

### Produkcja (Docker Compose)

Buduje wszystkie obrazy i uruchamia pełny stos. Kolejność startowania jest wymuszona przez healthchecki:
`db` → `backend` → `frontend`.

```bash
docker compose up --build
```

Aplikacja dostępna pod adresem: [http://localhost](http://localhost)

Zatrzymanie i usunięcie kontenerów:

```bash
docker compose down
# wraz z wolumenem bazy danych:
docker compose down -v
```

---

## Konfiguracja

### Backend – zmienne środowiskowe

Wartości domyślne zdefiniowane w `application.yml`. Można je nadpisać zmiennymi środowiskowymi lub plikiem `.env`.

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
# lub
java -jar app.jar --spring.profiles.active=dev
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

## Migracje bazy danych

Projekt używa **Flyway** do zarządzania schematem bazy. Skrypty migracji znajdują się w:

```
backend/src/main/resources/db/migration/
```

Konwencja nazewnictwa plików: `V{numer}__{opis}.sql`, np. `V1__init_schema.sql`.

Flyway uruchamia migracje automatycznie przy starcie aplikacji. Parametr `baseline-on-migrate: true` pozwala na migrację istniejącej bazy bez historii Flyway.

---

## Testy

```bash
cd backend
./mvnw test
```

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

---

## Healthcheck

Po uruchomieniu dostępny jest endpoint sprawdzający stan aplikacji:

```
GET /api/actuator/health
```

Przykładowa odpowiedź:
```json
{ "status": "UP" }
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
