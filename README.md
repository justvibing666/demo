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

Buduje wszystkie obrazy i uruchamia pełny stos.

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

| Zmienna              | Domyślna wartość | Opis                        |
|----------------------|------------------|-----------------------------|
| `DB_HOST`            | `localhost`      | Host bazy danych            |
| `DB_PORT`            | `5432`           | Port bazy danych            |
| `DB_NAME`            | `demo_db`        | Nazwa bazy danych           |
| `DB_USER`            | `demo_user`      | Użytkownik bazy danych      |
| `DB_PASSWORD`        | `demo_pass`      | Hasło do bazy danych        |
| `SERVER_PORT`        | `8080`           | Port serwera                |

### Profile Spring Boot

| Profil | Plik                      | Zastosowanie                   |
|--------|---------------------------|--------------------------------|
| domyślny | `application.yml`       | Wspólna konfiguracja           |
| `dev`  | `application-dev.yml`     | Logowanie SQL, devtools        |

Aktywacja profilu:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# lub
java -jar app.jar --spring.profiles.active=dev
```

---

## API

Backend wystawia REST API pod ścieżką `/api`.

| Metoda   | Endpoint            | Opis                        |
|----------|---------------------|-----------------------------|
| `GET`    | `/api/examples`     | Lista wszystkich rekordów   |
| `GET`    | `/api/examples/{id}`| Pobranie rekordu po ID      |
| `POST`   | `/api/examples`     | Utworzenie nowego rekordu   |
| `DELETE` | `/api/examples/{id}`| Usunięcie rekordu           |
| `GET`    | `/api/actuator/health` | Status aplikacji         |

---

## Migracje bazy danych

Projekt używa **Flyway** do zarządzania schematem bazy. Skrypty migracji znajdują się w:

```
backend/src/main/resources/db/migration/
```

Konwencja nazewnictwa plików: `V{numer}__{opis}.sql`, np. `V1__init_schema.sql`.

Flyway uruchamia migracje automatycznie przy starcie aplikacji.

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
