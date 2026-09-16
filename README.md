# Music App Backend

A music streaming REST API built with **Java 21 and Spring Boot 3.5**, covering music discovery, personal libraries, artist publishing, and administrative content moderation. PostgreSQL stores application data, Cloudinary hosts audio and images, and Redis caches frequently requested catalog results.

This repository contains the backend. A separate frontend consumes its APIs; the complete integration contract is documented in [Frontend API Guide](docs/FRONTEND_API_GUIDE.md).

## Features

| Area | Functionality |
| --- | --- |
| Music catalog | Browse songs, albums, and artists; retrieve lyrics and cover images; filter songs by artist or album with pagination and sorting |
| Audio playback | Retrieve stream metadata or redirect the browser audio player to Cloudinary-hosted media |
| Personal library | Create and edit playlists, add or remove tracks, like songs, follow artists, and view listening history |
| Discovery | Search across songs, albums, and artists; retrieve top songs, new releases, and personalized recommendations |
| Accounts | Register, log in, refresh sessions, update profiles and avatars, change passwords, and deactivate accounts |
| Artist Studio | Apply for an artist account; manage owned albums and songs; upload audio and artwork; submit content for review |
| Administration | Review artist applications, approve or reject submitted content, disable published content, and manage catalog entries |

Personalized recommendations use recent listening history to identify preferred artists and albums, exclude recently played tracks, and fill remaining slots with popular songs. Users without history receive popular tracks. This is a history-based ranking strategy, not a machine learning model.

## Technology and implementation

| Technology | Purpose |
| --- | --- |
| Java 21, Spring Boot 3.5.14 | REST controllers, application services, configuration, and dependency injection |
| Spring Security, JJWT | Bearer access tokens, HttpOnly refresh cookies, and role-based authorization for USER, ARTIST, and ADMIN |
| PostgreSQL, Spring Data JPA / Hibernate | Relational persistence for users, catalog entries, playlists, follows, and playback history |
| Flyway | Versioned schema migrations; Hibernate validates the database schema at startup |
| Spring Cache, Redis | Cached catalog reads with expiration and cache invalidation after relevant mutations |
| Cloudinary | Multipart upload and hosting of audio, artwork, and avatars |
| Jakarta Validation | Request validation, combined with application-level ownership and lifecycle checks |
| Springdoc OpenAPI, Swagger UI | API discovery and interactive documentation |
| JUnit 5, Spring Boot Test, Spring Security Test | Automated API, authorization, persistence, and business workflow tests |
| Embedded PostgreSQL, JaCoCo | Disposable PostgreSQL for tests and generated coverage reports |
| Docker Compose, GitHub Actions | Local service orchestration, automated verification, image builds, and optional GHCR publishing |

### Architecture

The code is organized around Clean Architecture principles with presentation, application, domain, and infrastructure packages. Repository interfaces separate core use cases from persistence and media providers. Some creator workflows currently access JPA repositories directly, so dependency isolation is not yet uniform across every feature.

```text
src/main/java/com/spotify/spotify_backend/
├── presentation/     # REST controllers, request middleware, exception handling
├── application/      # Use cases, services, request/response DTOs, mappers
├── domain/           # Domain models, repository contracts, business exceptions
└── infrastructure/   # JPA adapters, JWT, Cloudinary, Redis, configuration

src/main/resources/db/migration/  # Flyway migrations
src/test/                        # Automated tests
```

### Authorization and publishing

Playlist mutations require ownership. Artist Studio resolves the artist from the authenticated user and restricts mutations to that artist's content. Administrative routes require ADMIN; Artist Studio routes require ARTIST.

Content follows this lifecycle:

```text
DRAFT → PENDING_REVIEW → PUBLISHED → DISABLED
                      ↘ REJECTED → edit → DRAFT
```

Only draft or rejected content can be edited, deleted, or submitted by its artist. Album approval and song approval are separate operations. Artist account applications use a separate PENDING / APPROVED / REJECTED workflow.

## Run with Docker

Requirements: Docker Desktop or Docker Engine with Compose.

1. Copy `.env.example` to `.env`.
2. Set `DB_PASSWORD` and a random `JWT_SECRET` of at least 64 characters.
3. Set `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, and `CLOUDINARY_API_SECRET` to enable media uploads.
4. Start the backend and its PostgreSQL/Redis dependencies:

```sh
docker compose up backend --build --wait --wait-timeout 180
```

Selecting `backend` allows this repository to run independently. Starting every Compose service also builds a frontend from `../spotify_clone_front`, which must exist separately.

| Service | Local address |
| --- | --- |
| Backend | `http://localhost:8000` by default; configurable through `PORT` |
| Health | `http://localhost:8000/actuator/health` |
| Swagger UI (development) | `http://localhost:8000/swagger-ui/index.html` |
| OpenAPI JSON (development) | `http://localhost:8000/v3/api-docs` |
| PostgreSQL / pgAdmin connection | Host `localhost`, port `POSTGRES_PORT`, database `music_app`, user `postgres`, password `DB_PASSWORD` |

The main Compose file defaults PostgreSQL's host port to `5433`; the supplied `.env.example` sets `POSTGRES_PORT=5432`. Use the actual value in `.env` when configuring pgAdmin. PostgreSQL binds to loopback; Redis is reachable within the Compose network. Named volumes preserve database and cache data between container restarts.

## Run with Maven

Requirements: Java 21 and PostgreSQL. The Maven wrapper is included. Redis is optional for local Maven runs unless Redis caching is enabled.

To run only the supporting services locally:

```sh
docker compose -f docker-compose.local.yml up -d --wait
```

This local Compose file uses separate volumes from the main stack. It is an alternative local setup, not a connection to an already running main-stack database.

Set environment variables in the terminal or IDE before running Spring Boot. Maven does **not** automatically load `.env`.

| Variable | Purpose |
| --- | --- |
| `DB_URL` | JDBC URL for the database, for example `jdbc:postgresql://localhost:5432/music_app` |
| `DB_USERNAME`, `DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | JWT signing secret |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | Media provider credentials |
| `PORT` | HTTP port; defaults to 8000 |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins |
| `CACHE_TYPE` | Defaults to `none`; set to `redis` for local Redis caching |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` | Redis connection settings |

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```sh
bash ./mvnw spring-boot:run
```

The default profile is `dev`. The `prod` profile requires external service configuration and enables Redis caching. OpenAPI documentation is disabled by default in production and can be enabled with `API_DOCS_ENABLED=true`. Configure refresh-cookie security and allowed origins for the deployment environment.

Flyway applies schema migrations on startup. Development seeding is optional, requires explicit configuration, and creates no songs or media; see [Development seed](docs/development_seed.md).

## Frontend integration

See [Frontend API Guide](docs/FRONTEND_API_GUIDE.md) for all 76 controller operations, request fields, TypeScript response types, access rules, and screen-by-screen integration guidance.

| API group | Examples |
| --- | --- |
| Authentication | `POST /api/auth/login/`, `POST /api/auth/refresh/` |
| Catalog | `GET /api/songs/filter`, `GET /api/albums/{id}/songs/` |
| Search and discovery | `GET /api/search?keyword=...`, `GET /api/recommendations/personalized` |
| Playback | `GET /api/stream/song/{id}/play`, `POST /api/recently-played/play` |
| Library | `/api/playlists`, `/api/favorites`, `/api/artist-follows` |
| Artist workflows | `/api/artist-applications`, `/api/artist/albums`, `/api/artist/songs` |
| Moderation | `/api/admin/artist-applications`, `/api/admin/songs/pending`, `/api/admin/albums/pending` |

Responses are returned directly as objects, arrays, or paginated results. Authenticated requests use `Authorization: Bearer <accessToken>`. Authentication calls use credentials to carry the HttpOnly refresh cookie. Follow the documented trailing slashes exactly.

The browser plays media from Cloudinary; the backend does not proxy audio bytes. Playback history is recorded through a separate authenticated POST. Playback queue, shuffle, repeat, and volume are frontend state. Lyrics are plain text, and duration is currently supplied as metadata rather than automatically extracted during upload.

## Testing

```powershell
.\mvnw.cmd -B -ntp clean verify
```

On Linux/macOS, use `bash ./mvnw -B -ntp clean verify`.

Tests use a disposable PostgreSQL instance and the application's Flyway migrations. Alternatively, configure `TEST_DB_URL`, `TEST_DB_USERNAME`, and `TEST_DB_PASSWORD` for a dedicated test database. Tests write and clean up data, so this must not point to the application database.

Set `TEST_REDIS_HOST` and optionally `TEST_REDIS_PORT` to enable the real Redis integration test against a dedicated test instance. Without this configuration, that test is skipped.

Latest local verification on **16 September 2026**: `mvnw verify` completed successfully with **130 tests discovered, 129 passed, 1 Redis integration test skipped, and no failures or errors**. This is a local verification result, not a claim about the latest remote CI run.

- Test reports: `target/surefire-reports/`
- Coverage report: `target/site/jacoco/index.html`

## CI and container publishing

- [Backend CI](.github/workflows/ci.yml): runs Maven verification with PostgreSQL and Redis, uploads test/coverage reports, builds the Docker image, and smoke-tests the backend Compose services.
- [Publish workflow](.github/workflows/publish.yml): manually triggered verification and image publishing to GitHub Container Registry.

Credentials belong in environment variables or local `.env` files. Local media, database exports, build output, and secrets are excluded from Git. Upload actual music and artwork through the application APIs; they are not distributed with this repository.
