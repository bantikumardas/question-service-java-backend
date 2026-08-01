# Question Service

A Spring Boot backend for managing coding/MCQ questions, tests, and timed exam sessions — including test creation, candidate invites, exam execution, and code running.

## Tech Stack

- **Java 21** / **Spring Boot 3.5.15**
- **Spring Web** — REST APIs
- **Spring Data JPA** + **H2** (file-based) — persistence
- **Flyway** — database migrations
- **Spring Data Redis** (Lettuce) — caching (e.g. exam session state)
- **Spring Security** + **JJWT** — authentication/authorization via JWT
- **Spring Mail** — test invite emails
- **Spring AOP** / **Spring Retry** — cross-cutting concerns and retry logic
- **Lombok**

## Project Structure

```
src/main/java/com/question/service/question_service/
├── config/         # Security, Redis, Async configuration
├── controller/      # REST controllers
├── dto/             # Request/response/cache DTOs
├── exception/       # Custom exceptions + global exception handler
├── models/          # JPA entities
├── repository/       # Spring Data repositories
├── security/         # JWT filter/util, UserDetailsService
├── service/          # Service interfaces + impl/
└── utils/

src/main/resources/
├── application.yaml
└── db/migration/     # Flyway SQL migrations
```

## API Overview

| Area | Base path | Endpoints |
|---|---|---|
| Auth | `/api/auth` | `POST /register`, `POST /login`, `POST /logout` |
| Tests | `/api/tests` | `POST /create`, `GET /all-tests`, `GET /{testId}`, `POST /{testId}/change-status`, `POST /send/invite/{testId}` |
| Questions | `/question` | `POST /one/mcq`, `POST /one/coding`, `POST /one/testcase` |
| Exam | `/exam` | `POST /start` |
| Code Runner | `/api/code-run` | `POST /run` |

## Configuration

Configuration lives in `src/main/resources/application.yaml` and is overridable via environment variables (loaded from a local `.env` via `spring-dotenv`):

| Variable | Purpose | Default |
|---|---|---|
| `SERVER_PORT` | HTTP port | `8080` |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis connection | `localhost` / `6379` / _empty_ |
| `SMTP_HOST` / `SMTP_PORT` / `SMTP_USER` / `SMTP_PASSWORD` | Mail server for test invites | `localhost` / `587` |
| `INVITE_MAIL_FROM` | From-address for invite emails | `no-reply@question-service.com` |
| `INVITE_POOL_CORE` / `INVITE_POOL_MAX` / `INVITE_POOL_QUEUE` | Invite dispatcher thread pool sizing | `5` / `20` / `500` |
| `JWT_SECRET` | JWT signing secret | dev default (override in prod) |
| `JWT_EXPIRATION_MS` | JWT expiration (ms) | `86400000` |
| `FRONEND_BASE_URL` | Frontend base URL (used in invite links, etc.) | _required_ |

The H2 database file is stored at `~/.question-service/question_db`, and its console is available at `/h2-console` when the app is running.

## Getting Started

### Prerequisites

- Java 21
- A running Redis instance
- An SMTP server (for sending test invites)

### Run

```bash
./mvnw spring-boot:run
```

### Test

```bash
./mvnw test
```

### Build

```bash
./mvnw clean package
```

Flyway migrations under `src/main/resources/db/migration` run automatically on startup.
