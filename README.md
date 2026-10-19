# CareerFlow

A REST backend for managing a job search: track applications and interviews, and study interview questions with spaced repetition. Built with Spring Boot, PostgreSQL and JWT authentication.

> This repository currently contains the backend only (`backend/`) and the local infrastructure definition (`infrastructure/`). There is no front end here.

## Features

What is implemented in the code today:

- **Authentication.** Register and sign in with email and password. Passwords are stored as BCrypt hashes; a successful call returns a signed JWT (HS256) that is sent as `Authorization: Bearer <token>`. Sessions are stateless.
- **Application tracking (ATS).** Create, list (paginated and sorted), read, update and delete job applications. Each application has a company, position, status (`WISHLIST`, `APPLIED`, `INTERVIEWING`, `OFFER`, `REJECTED`), location, salary range, applied date and notes.
- **Interview log.** Attach interviews to an application: round (`RECRUITER_SCREEN`, `TECHNICAL`, `SYSTEM_DESIGN`, `MANAGER`, `ON_SITE`, `FINAL`), date and time, interviewer name, meeting link, notes and a follow-up flag. List an application's interviews and delete one.
- **Flashcards with spaced repetition (SRS).** Create decks and flashcards, list a deck's cards and its due cards, and record a study result with a quality score from 0 to 5. Scheduling follows the SM-2 algorithm: a score below 3 resets the card (next review in 1 day), otherwise the interval grows 1 day, 6 days, then interval times easiness factor; the easiness factor is updated on every review and never drops below 1.3.
- **Per-user data.** Every record is owned by a user: applications and decks directly, interviews through their application, flashcards through their deck. The service layer checks ownership on every operation by id (see "Ownership model" below).
- **Consistent API envelope and errors.** Every response is `{ success, message, data, timestamp }`. Bean Validation errors return a field-to-message map; not found, business rule, bad credentials, access denied and duplicate email map to 404, 400, 401, 403 and 409.
- **API documentation.** OpenAPI 3 with Swagger UI, including a bearer-token security scheme.
- **Cross-cutting.** Database schema managed by Flyway migrations (Hibernate runs in `validate` mode), MapStruct DTO mappers, and an AOP logging aspect over controllers, services and repositories.

## Tech stack

Versions are taken from `backend/pom.xml`.

| Area | Technology |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.5.9 (Web, Data JPA, Validation, Security, AOP) |
| Database | PostgreSQL (`postgres:17-alpine` in Docker Compose) |
| Migrations | Flyway (`flyway-core`, `flyway-database-postgresql`, version managed by Spring Boot) |
| Auth | Spring Security, JJWT 0.11.5, BCrypt |
| Mapping / boilerplate | MapStruct 1.5.5.Final, Lombok 1.18.30 |
| API docs | springdoc-openapi 2.8.3 (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring MockMvc, Testcontainers (PostgreSQL), JaCoCo 0.8.11 |
| Quality | SonarScanner for Maven 3.10.0.2594; a SonarQube Community container is defined in Docker Compose |
| Build | Maven (wrapper pinned to Maven 3.9.12) |

## Architecture

Layered Spring application; the base package is `com.careerflow.backend` (`backend/src/main/java/...`).

```
controller/   REST controllers (auth, applications, interviews, decks, flashcards)
service/      service interfaces, service/impl/ implementations (business rules, SM-2)
repository/   Spring Data JPA repositories
model/        JPA entities (User, Application, Interview, Deck, FlashCard), enums, BaseEntity (id, timestamps)
dto/          request and response DTOs, global ApiResponse envelope
mapper/       MapStruct mappers
security/     JwtService, JwtAuthenticationFilter
config/       SecurityConfig (filter chain, CORS), ApplicationConfig (auth beans), OpenApiConfig, WebConfig
exception/    business exceptions and GlobalExceptionHandler
aspect/       LoggingAspect
util/         SecurityUtils (current user from the security context)
resources/db/migration/   Flyway: V1 schema, V2 users, V3 user ownership columns
```

Data model: `users` own `applications` and `decks` (`user_id`); an application has many `interviews`; a deck has many `flashcards`.

Security: all routes require a valid JWT except `/api/v1/auth/**`, `/v3/api-docs/**` and Swagger UI. CSRF is disabled (stateless API). CORS allows `http://localhost:5173` and `http://localhost:3000` by default (override with `app.cors.allowed-origins`). Roles `USER` and `ADMIN` exist on the user; no endpoint is restricted by role yet.

**Ownership model.** Ownership is enforced in the service layer, not only in the controllers. Every by-id lookup is scoped to the authenticated user (`findByIdAndUserId`, `findByIdAndApplicationUserId`, `findByIdAndDeckUserId`), and creating a child (an interview or a card) first resolves its parent through the same scoped lookup. A record that belongs to someone else answers **404**, exactly like a record that does not exist, so ids cannot be probed for existence. `OwnershipIT` runs this with two users against every by-id endpoint.

**Logging policy.** `LoggingAspect` traces controller, service and repository calls at `DEBUG` only. It logs the method name, argument types, plain numeric, boolean, UUID and enum arguments (such as ids) and the duration. It never logs strings, request DTOs or return values, so passwords and JWTs cannot reach the log through it, and failures log the exception type only. Credential fields are also excluded from `toString()`, the unexpected-error handler returns a generic message (details stay in the server log), and `spring.jpa.show-sql` is off. `LoggingAspectIT` captures all log output at TRACE during register and login and asserts the password and token never appear.

## Running locally

Requirements: Java 21, Docker with Docker Compose.

### 1. Start the database

Docker Compose reads its variables from `infrastructure/.env` (git-ignored; create it yourself). The variable **names** are:

| Used by | Variables |
|---|---|
| PostgreSQL | `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB` |
| pgAdmin | `PGADMIN_EMAIL`, `PGADMIN_PASSWORD` |
| SonarQube and its database | `SONAR_DB`, `SONAR_USER`, `SONAR_PASSWORD` |

```bash
docker compose -f infrastructure/docker-compose.yml up -d postgres
```

PostgreSQL is published on host port `5433`. The compose file also defines pgAdmin (port 5050), SonarQube (port 9000) and its database; start them only if you need them.

### 2. Configure the backend

`backend/src/main/resources/application.yml` takes everything sensitive from the environment. Set these variables in your shell or IDE run configuration (the backend does not read `.env` itself):

| Variable | Meaning |
|---|---|
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Database name and credentials; use the same values as `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`. The JDBC URL is `jdbc:postgresql://localhost:5433/<DB_NAME>`. |
| `JWT_SECRET` | Base64-encoded signing key, at least 256 bits (it is Base64-decoded before use). |
| `JWT_EXPIRATION` | Token lifetime in milliseconds. |

### 3. Start the API

```bash
cd backend
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

Flyway creates the schema on first start. The API is at `http://localhost:8080` (Spring Boot default port; not overridden in `application.yml`). Swagger UI: `http://localhost:8080/swagger-ui.html`, OpenAPI JSON: `http://localhost:8080/v3/api-docs`.

Quick check:

```bash
# register (returns a token)
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com","password":"<choose-a-password>"}'

# use the token
curl http://localhost:8080/api/v1/applications -H "Authorization: Bearer <token>"
```

## API overview

Base path `/api/v1`. All routes except the two auth routes need `Authorization: Bearer <token>`.

| Method | Path | Description |
|---|---|---|
| POST | `/auth/register` | Create a user (`firstName`, `lastName`, `email`, `password`); returns a token |
| POST | `/auth` | Sign in (`email`, `password`); returns a token |
| POST | `/applications` | Create an application (201) |
| GET | `/applications` | List the current user's applications; Spring pageable parameters `page`, `size` (default 10), `sort` (default `createdAt,desc`) |
| GET | `/applications/{id}` | Get one application |
| PUT | `/applications/{id}` | Update an application |
| DELETE | `/applications/{id}` | Delete an application (its interviews are removed with it) |
| POST | `/applications/{applicationId}/interviews` | Add an interview (201) |
| GET | `/applications/{applicationId}/interviews` | List an application's interviews |
| DELETE | `/interviews/{id}` | Delete an interview |
| POST | `/decks` | Create a deck (201) |
| GET | `/decks` | List the current user's decks (paginated, with `totalCards`) |
| DELETE | `/decks/{id}` | Delete a deck (its cards are removed with it) |
| POST | `/flashcards` | Create a flashcard (`deckId`, `frontContent`, `backContent`) (201) |
| GET | `/decks/{deckId}/cards` | List a deck's cards |
| GET | `/decks/{deckId}/due-cards` | List cards whose next review date has passed |
| POST | `/flashcards/{cardId}/study?quality=0..5` | Record a review and reschedule the card (SM-2) |
| DELETE | `/flashcards/{id}` | Delete a flashcard |

A new card has no review date until it has been studied once, so it does not appear in the due list before that.

## Tests

```bash
cd backend
./mvnw test                          # unit and integration tests; Docker must be running
```

- Unit tests cover the application, interview and flashcard services (including the SM-2 rules and the quality range check).
- Integration tests (`*IT`: ownership, logging, auth and error handling, application and flashcard flows) share one throw-away `postgres:17` container and one Spring context through `AbstractIntegrationTest` (**Testcontainers**), so Docker must be running. Surefire is configured to include `*IT`, so plain `./mvnw test` runs everything.
- Tests read test-only JWT settings from `backend/src/test/resources/application.properties` and the database from the container, so no environment variables are needed to run them. Running the app itself still needs `JWT_SECRET` (Base64, at least 256 bits; startup fails otherwise) and the other variables listed above.
- JaCoCo writes a coverage report to `backend/target/site/jacoco` after `test`.

## Project status and limitations

This is a work-in-progress backend. Honest list of what is missing or rough:

- Records created before the ownership columns existed (`user_id` is nullable in the schema) belong to nobody and are unreachable through the API; there is no backfill or `NOT NULL` migration yet.
- Authentication is minimal: no refresh tokens, no logout or token revocation, no rate limiting or lockout on sign-in, and roles are not used for authorization.
- Swagger UI and the OpenAPI document are public, JJWT is on 0.11.5 (older builder API), and JPA entities use Lombok `@Data` (generated `equals`/`hashCode`/`toString` over lazy relations is fragile).
- Missing endpoints: no update for decks, flashcards or interviews, no get-by-id for decks, and the card and interview lists are not paginated.
- No front end, no CI workflow, no Dockerfile for the backend in this repository, and no license file.
- Some validation messages are in Turkish, others in English.

## Türkçe özet

CareerFlow, iş arama sürecini yönetmek için yazılmış bir REST backend'idir (Java 21, Spring Boot 3.5.9, PostgreSQL, JWT). İş başvurularını ve mülakatları takip etmenizi, ayrıca mülakat sorularını SM-2 algoritmasıyla aralıklı tekrar kartları (flashcard) olarak çalışmanızı sağlar. Kimlik doğrulama JWT ile yapılır, şema Flyway ile yönetilir, API dokümantasyonu Swagger UI üzerinden açılır, entegrasyon testleri Testcontainers ile çalışır.

Çalıştırmak için: `infrastructure/.env` dosyasını oluşturup `docker compose -f infrastructure/docker-compose.yml up -d postgres` ile veritabanını başlatın, ardından `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (Base64) ve `JWT_EXPIRATION` (milisaniye) ortam değişkenlerini tanımlayıp `backend` klasöründe `./mvnw spring-boot:run` komutunu çalıştırın. Swagger arayüzü `http://localhost:8080/swagger-ui.html` adresindedir.

Durum: devam eden bir projedir. Başlıca eksikler: tüm id ile erişimlerde kullanıcıya göre sahiplik kontrolü yapılır (başkasının kaydı 404 döner) ve loglarda parola/token tutulmaz; eksikler: oturum yönetimi sade, ön yüz ve CI bu depoda yok. Ayrıntılar yukarıdaki "Project status and limitations" bölümündedir.
