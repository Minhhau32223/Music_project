# Spotify Clone - Music App Backend API

> A robust, scalable, enterprise-grade music streaming REST API built with Spring Boot 3, Clean Architecture, and PostgreSQL. Powers audio streaming, JWT security, playlist management, artist onboarding, and content moderation.

[![Java](https://img.shields.io/badge/Java-17%2B%20%2F%2021-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=flat-square&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migration-CC0200?style=flat-square&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-Media%20CDN-3448C5?style=flat-square&logo=cloudinary&logoColor=white)](https://cloudinary.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)](LICENSE)

---

## Table of Contents

- [Project Overview](#1-project-overview)
- [Features](#2-features)
- [Screenshots](#3-screenshots)
- [System Architecture](#4-system-architecture)
- [Tech Stack](#5-tech-stack)
- [Project Structure](#6-project-structure)
- [Getting Started](#7-getting-started)
- [Environment Variables](#8-environment-variables)
- [API Documentation](#9-api-documentation)
- [Testing](#10-testing)
- [Deployment](#11-deployment)
- [Roadmap](#12-roadmap)
- [Contributing](#13-contributing)
- [License](#14-license)
- [Contact](#15-contact)
- [Acknowledgements](#acknowledgements)

---

## 1. Project Overview

### 1.1 Introduction

**Spotify Clone Backend** is a high-performance RESTful API service developed to power modern music streaming platforms. Built on the principles of **Clean Architecture** and **Domain-Driven Design**, it handles user identity, catalog discovery, audio metadata extraction, multi-tier role authorization, playlist coordination, artist workspace management, and media content delivery.

- **Problem Statement:** Modern music streaming services require handling complex asynchronous media workflows, high-concurrency read operations (popular charts, recommendations), precise audio duration calculations from uploaded files, strict role-based content moderation, and silent token refresh authorization without compromising security or responsiveness.
- **Solution:** A decoupled, stateless Spring Boot 3 architecture utilizing Spring Data JPA with PostgreSQL, Flyway automated migrations, Redis caching, and Cloudinary CDN for streaming binary assets.
- **Target Users:** Music listeners, independent content creators/artists, and platform administrators.
- **Main Objectives:** Ensure 100% stateless JWT authentication, sub-second API response times, automated audio duration extraction, and secure content moderation.

### 1.2 Project Objectives

- **Clean Architecture Separation:** Decouple domain entities and business use cases from infrastructure frameworks and HTTP presentation layers.
- **Zero-Trust Security & RBAC:** Enforce Role-Based Access Control (`USER`, `ARTIST`, `ADMIN`) across all endpoints with secure HttpOnly cookie-based refresh tokens.
- **Automated Media Pipelines:** Read true duration directly from MP3 audio metadata upon upload and stream files via Cloudinary CDN.
- **Reliable Database Versioning:** Maintain transparent, reproducible schema evolutions using Flyway migration scripts.

### 1.3 Project Scope

- **Included Features:** Authentication & token rotation, catalog CRUD (songs, albums, artists), discovery & multi-entity search, user library (favorites, playlists, artist follows, playback history), artist application workflow, artist upload studio, and admin content moderation.
- **Supported Platforms:** Containerized Linux/Alpine environments via Docker and cross-platform native execution (Windows, macOS, Linux).
- **Current Limitations:** Audio delivery operates via progressive download; audio transcoding to HLS/DASH is planned for future iterations.
- **Future Expansion:** WebSocket integration for real-time live sessions, payment gateway integration, and machine-learning recommendation pipelines.

### 1.4 Project Status

- **Current Stage:** Production-Ready REST API Service.
- **Implemented Modules:** 18 REST Controllers covering Authentication, Catalog, Library, Search, Media Streaming, Artist Studio, and Admin Moderation.
- **Known Limitations:** Redis caching is optional and disabled by default for simplified local development; enabled via environment configuration.

---

## 2. Features

### 2.1 Core Features

- **Audio Streaming & Play Count Tracking:** Stream published songs with range-request compatibility and automated incrementing of track play counts.
- **Automated Audio Metadata Extraction:** Analyzes uploaded MP3 binaries to determine exact track duration in seconds, preventing inaccurate client-supplied durations.
- **Synchronized Lyrics Support:** Stores and serves time-stamped lyrics for song detail views.
- **Cloud Media Uploads:** Uploads cover artwork, artist avatars, and MP3 files directly to Cloudinary storage.

### 2.2 User Features

- **Authentication & Sessions:** User registration, password encryption via BCrypt, JWT access token issuance, and HttpOnly refresh token rotation.
- **Custom Playlists:** Create, update, delete playlists, and manage songs within playlists with custom order.
- **Favorites & Follows:** Heart/unheart songs and follow/unfollow favorite artists with instant query synchronization.
- **Playback History:** Automatically records recently played songs with timestamps for rapid retrieval.
- **Multi-Entity Search:** Query songs, artists, and albums simultaneously with sorting and pagination parameters.

### 2.3 Administrative & Creator Features

- **Artist Onboarding Workflow:** Standard users apply for artist status by submitting bio, portfolio, and avatar.
- **Artist Workspace:** Approved artists can manage their discography, create albums, and upload new songs with audio files and lyrics.
- **Content Moderation:** Platform administrators inspect pending song uploads, review lyrics and audio files, and approve or reject submissions with reason notes.
- **Artist Verification:** Admin review portal for approving or declining pending artist applications.

### 2.4 Additional Features

- **Automated Database Migrations:** Schema changes versioned and executed automatically via Flyway (`V1` through `V5`).
- **Interactive OpenAPI 3 / Swagger UI:** Interactive endpoint exploration and contract testing.
- **Centralized Error Normalization:** Consistent JSON error response envelopes (`timestamp`, `status`, `error`, `message`, `path`).

---

## 3. Screenshots

### 3.1 Architecture Overview

```
+-----------------------------------------------------------------------------+
|                               Presentation Layer                            |
|       AuthController | SongController | ArtistController | AdminController  |
+--------------------------------------+--------------------------------------+
                                       | DTOs / Commands
                                       v
+-----------------------------------------------------------------------------+
|                               Application Layer                             |
|       Use Cases | Service Interfaces | Business Rules | Data Mappers        |
+--------------------------------------+--------------------------------------+
                                       | Domain Contracts
                                       v
+-----------------------------------------------------------------------------+
|                                 Domain Layer                                |
|          Domain Entities | Value Objects | Repository Interfaces            |
+--------------------------------------+--------------------------------------+
                                       ^ Implements
                                       |
+-----------------------------------------------------------------------------+
|                             Infrastructure Layer                            |
|    Spring Data JPA | PostgreSQL | Redis Cache | Cloudinary Storage | Flyway |
+-----------------------------------------------------------------------------+
```

### 3.2 Swagger UI Interface

```
+-----------------------------------------------------------------------------+
|  Swagger UI - Music App API Documentation                v3.5 / OpenAPI 3.0 |
+-----------------------------------------------------------------------------+
|  [POST]   /api/auth/login                  Authenticate user credentials    |
|  [POST]   /api/auth/refresh                Refresh access token via cookie  |
|  [GET]    /api/songs                       Get paginated song catalog       |
|  [GET]    /api/songs/{id}                  Get song details with lyrics     |
|  [POST]   /api/artist/songs                Upload new song track            |
|  [GET]    /api/admin/applications          List pending artist applications |
+-----------------------------------------------------------------------------+
```

---

## 4. System Architecture

### 4.1 Architecture Overview

The backend is architected following **Clean Architecture (Onion Architecture)** principles. Core domain models and business logic remain strictly independent of external frameworks, libraries, database engines, and UI layers.

### 4.2 Main Components

- **Domain Layer (`domain`):** Pure Java entities, value objects, domain exceptions, and repository interface specifications. No Spring or Hibernate dependencies.
- **Application Layer (`application`):** Use cases, service implementations, input/output DTOs, and mapping logic. Coordinates business transactions.
- **Infrastructure Layer (`infrastructure`):** Spring Data JPA repositories, PostgreSQL entity mappings, Cloudinary storage adapters, Redis caching implementations, and Flyway migration scripts.
- **Presentation Layer (`presentation`):** Spring MVC REST controllers, Spring Security filters, JWT authentication token provider, CORS policy configurations, and `@RestControllerAdvice` error handlers.

### 4.3 Request Flow

1. **Inbound HTTP Request:** Client submits an HTTP request to `/api/**`.
2. **Security & Authentication Filter:** Spring Security validates the `Authorization: Bearer <JWT>` header or refresh token cookie.
3. **Controller Handling:** The matched REST controller deserializes the request body, validates DTO constraints (Jakarta Validation), and delegates to the appropriate use case service.
4. **Business Execution:** The application service coordinates domain rules, calls repository interfaces, and interfaces with Cloudinary if media processing is needed.
5. **Persistence:** JPA repository persists changes to PostgreSQL within transactional boundaries (`@Transactional`).
6. **Outbound Response:** Controller maps domain output to a standardized response DTO and returns standard HTTP status codes.

### 4.4 Design Principles

- **Dependency Inversion:** High-level policy modules do not depend on low-level detail modules; both depend on abstractions.
- **Single Responsibility Principle:** Controllers only route and serialize; services strictly implement business workflows.
- **Statelessness:** No session state is held in server memory, allowing seamless horizontal scaling behind a load balancer.

---

## 5. Tech Stack

### 5.1 Backend Framework & Security

| Technology | Version | Purpose |
|---|---|---|
| [Java](https://www.oracle.com/java/) | 17+ / 21 | Strongly-typed modern programming language |
| [Spring Boot](https://spring.io/projects/spring-boot) | 3.5.x | Core application framework and dependency injection container |
| [Spring Security](https://spring.io/projects/spring-security) | 6.x | RBAC security, JWT filter chains, CORS configuration |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | 3.5.x | Database persistence and repository abstractions |
| [Hibernate](https://hibernate.org/) | 6.x | Object-Relational Mapping (ORM) engine |
| [JJWT (Java JWT)](https://github.com/jwtk/jjwt) | 0.12.x | Cryptographic creation and parsing of JSON Web Tokens |

### 5.2 Database, Migration & Caching

| Technology | Purpose |
|---|---|
| [PostgreSQL 16](https://www.postgresql.org/) | Primary relational database for application data |
| [Flyway](https://flywaydb.org/) | Automated database schema version control and migrations |
| [Redis](https://redis.io/) | Optional high-throughput in-memory caching and session support |

### 5.3 Media Storage & Third-Party Services

| Technology | Purpose |
|---|---|
| [Cloudinary](https://cloudinary.com/) | Cloud storage, metadata inspection, and streaming CDN for MP3s and images |
| [mp3agic / JAudioTagger](https://github.com/mpatric/mp3agic) | Native audio metadata parsing to extract exact duration |

### 5.4 Development & Documentation Tools

| Technology | Purpose |
|---|---|
| [Maven Wrapper](https://maven.apache.org/) | Deterministic build tool and dependency management |
| [Lombok](https://projectlombok.org/) | Java boilerplate reduction (getters, setters, builders) |
| [Springdoc OpenAPI / Swagger UI](https://springdoc.org/) | Automated OpenAPI 3.0 interactive documentation |
| [Docker & Docker Compose](https://www.docker.com/) | Multi-service local virtualization and deployment packaging |

---

## 6. Project Structure

```text
spotify_clone_back/
├── src/main/java/com/spotify/spotify_backend/
│   ├── domain/                         # Pure domain layer
│   │   ├── entity/                     # Domain business entities
│   │   ├── repository/                 # Domain repository contracts
│   │   └── exception/                  # Domain-specific exceptions
│   │
│   ├── application/                    # Application use case layer
│   │   ├── dto/                        # Request and response data transfer objects
│   │   ├── mapper/                     # Model-to-DTO conversion mappers
│   │   └── usecase/                    # Business use case service interfaces & implementations
│   │
│   ├── infrastructure/                 # Infrastructure and external adapters
│   │   ├── persistence/                # JPA entities, Spring Data repositories
│   │   ├── security/                   # JWT provider, security filters, BCrypt hasher
│   │   ├── storage/                    # Cloudinary media storage client
│   │   └── cache/                      # Redis cache configurations
│   │
│   └── presentation/                   # HTTP web presentation layer
│       ├── controller/                 # REST controllers (Auth, Song, Playlist, Admin, etc.)
│       ├── advice/                     # Global exception handler (@RestControllerAdvice)
│       └── config/                     # WebMvc, CORS, and OpenAPI Swagger configurations
│
├── src/main/resources/
│   ├── db/migration/                   # Flyway database schema scripts (V1 -> V5)
│   ├── application.yml                 # Main configuration profiles
│   └── application-local.yml           # Local development profile settings
│
├── src/test/java/                      # JUnit 5 & Spring Boot integration test suite
├── Dockerfile                          # Multi-stage production container build
├── docker-compose.yml                  # Multi-service composition (App, PostgreSQL, Redis)
├── pom.xml                             # Maven project descriptor
└── README.md                           # Backend documentation
```

### Directory Description

| Directory/File | Description |
|---|---|
| `domain/` | Core enterprise rules and entity structures, entirely framework-independent |
| `application/` | Application-specific business use cases orchestrating domain entities |
| `infrastructure/` | Framework implementations: PostgreSQL JPA mappings, Cloudinary, Redis |
| `presentation/` | REST API endpoints, request validation, and centralized error handling |
| `src/main/resources/db/migration/` | Version-controlled SQL migration scripts executed by Flyway |

---

## 7. Getting Started

### 7.1 Prerequisites

- **Java Development Kit (JDK):** Version 17 LTS or 21 LTS installed.
- **Maven:** Version 3.9+ (or use the included `./mvnw` / `.\mvnw.cmd` wrapper).
- **PostgreSQL:** Version 15 or 16 running locally (or via Docker).
- **Docker & Docker Compose:** Optional for running isolated containerized services.
- **Cloudinary Account:** Free account credentials for audio and image uploads.

### 7.2 Clone the Repository

```bash
git clone https://github.com/Minhhau32223/spotify_clone_back.git
cd spotify_clone_back
```

### 7.3 Configure Environment Variables

Copy the example configuration file:

```bash
cp .env.example .env
```

On Windows PowerShell:
```powershell
Copy-Item .env.example .env
```

Fill in your database credentials, JWT secret key, and Cloudinary API parameters. *(Do not commit `.env` to Git).*

### 7.4 Install Dependencies and Compile

Using the Maven Wrapper:

```bash
# Linux / macOS
./mvnw clean compile

# Windows PowerShell
.\mvnw.cmd clean compile
```

### 7.5 Database Setup

1. Start your local PostgreSQL server and create the application database:
   ```sql
   CREATE DATABASE spotify_clone;
   ```
2. Verify that `DB_USERNAME` and `DB_PASSWORD` in your `.env` file match your database credentials.
3. Flyway will automatically execute database migrations (`V1` through `V5`) when the Spring Boot application boots.

### 7.6 Run the Application

#### Option A: Running with Maven (Local Development)

```powershell
.\mvnw.cmd spring-boot:run
```
*(Application will start at `http://localhost:8000`)*

#### Option B: Running with Docker Compose

To start PostgreSQL, Redis, and the backend application together:

```bash
docker compose up -d --build
```

### 7.7 Verify the Installation

- **Health Check:** Open `http://localhost:8000/actuator/health` in your browser. It should return `{"status":"UP"}`.
- **Swagger Documentation:** Visit `http://localhost:8000/swagger-ui.html` to explore the interactive API.
- **Database Status:** Verify that PostgreSQL contains the migrated tables (`users`, `songs`, `albums`, `artists`, `playlists`, etc.).

---

## 8. Environment Variables

### 8.1 Configuration Reference

| Variable | Description | Required | Example |
|---|---|---|---|
| `DB_URL` | JDBC database connection URL | Yes | `jdbc:postgresql://localhost:5432/spotify_clone` |
| `DB_USERNAME` | PostgreSQL database username | Yes | `postgres` |
| `DB_PASSWORD` | PostgreSQL database password | Yes | `secure_password` |
| `JWT_SECRET` | Secret key used for signing HMAC-SHA256 JWT tokens | Yes | `superSecretJwtKeyWithSufficientEntropy2026!` |
| `JWT_EXPIRATION` | Access token lifespan in milliseconds | Yes | `900000` (15 mins) |
| `JWT_REFRESH_EXPIRATION` | Refresh token lifespan in milliseconds | Yes | `604800000` (7 days) |
| `CORS_ALLOWED_ORIGINS` | Permitted frontend origin URLs | Yes | `http://localhost:5173,https://yourdomain.com` |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary account cloud name | For Uploads | `my-cloud-name` |
| `CLOUDINARY_API_KEY` | Cloudinary API key | For Uploads | `123456789012345` |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | For Uploads | `abcdefghijklmnopqrstuvwx` |
| `PORT` | Application server port | Optional | `8000` |
| `CACHE_TYPE` | Cache provider (`none` or `redis`) | Optional | `none` |
| `REDIS_HOST` | Redis cache hostname | If Redis enabled | `localhost` |
| `REDIS_PORT` | Redis cache port | If Redis enabled | `6379` |

### 8.2 Security Notes

- **Never Commit Secrets:** Real passwords, JWT keys, and Cloudinary secrets must never be committed to Git. `.env` is ignored by `.gitignore`.
- **Environment Isolation:** Use distinct database credentials and separate JWT keys for development, staging, and production.
- **CORS Configuration:** Never use wildcard `*` origins when credentials (HttpOnly cookies) are allowed. Always specify exact frontend domains in `CORS_ALLOWED_ORIGINS`.
- **Credential Rotation:** Rotate compromised JWT secret keys and database credentials immediately.

---

## 9. API Documentation

### 9.1 API Overview

- **Base URL:** `http://localhost:8000/api`
- **Specification:** OpenAPI 3.0 (`/v3/api-docs`), interactive UI at `/swagger-ui.html`
- **Authentication Scheme:** `Authorization: Bearer <access_token>` header; refresh token managed via secure `HttpOnly` cookie.
- **Standard Error Response:**
  ```json
  {
    "timestamp": "2026-11-09T14:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Song with id 10 not found",
    "path": "/api/songs/10"
  }
  ```

### 9.2 API Endpoints

#### Authentication & User Identity

| Method | Endpoint | Description | Role / Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user account | Public |
| `POST` | `/api/auth/login` | Log in with credentials and receive JWT | Public |
| `POST` | `/api/auth/refresh` | Silently issue new access token using refresh cookie | Cookie |
| `POST` | `/api/auth/logout` | Invalidate token and clear refresh cookie | Bearer Token |
| `GET` | `/api/users/me` | Retrieve profile of the authenticated user | Bearer Token |

#### Catalog & Discovery

| Method | Endpoint | Description | Role / Auth |
|---|---|---|---|
| `GET` | `/api/songs` | Paginated song list with filter parameters | Public |
| `GET` | `/api/songs/{id}` | Get song metadata and synchronized lyrics | Public |
| `GET` | `/api/albums` | Browse published albums with pagination | Public |
| `GET` | `/api/albums/{id}` | Get album information and track list | Public |
| `GET` | `/api/artists` | Browse published artist profiles | Public |
| `GET` | `/api/artists/{id}` | Get artist details, top songs, and discography | Public |
| `GET` | `/api/search` | Search songs, albums, and artists simultaneously | Public |
| `GET` | `/api/recommendations` | Get personalized song recommendations | Public / User |
| `GET` | `/api/stream/{songId}` | Stream audio binary and record play count | Public |

#### User Library & Interactions

| Method | Endpoint | Description | Role / Auth |
|---|---|---|---|
| `GET` | `/api/favorites/me` | List user's favorite liked songs | `USER` |
| `POST` | `/api/favorites/{songId}` | Heart / unheart a song | `USER` |
| `GET` | `/api/playlists/me` | List playlists created by current user | `USER` |
| `POST` | `/api/playlists` | Create a new custom playlist | `USER` |
| `POST` | `/api/playlists/{id}/songs` | Add a song to a playlist | `USER` |
| `DELETE` | `/api/playlists/{id}/songs/{songId}` | Remove a song from a playlist | `USER` |
| `POST` | `/api/artist-follows/{artistId}` | Follow or unfollow an artist | `USER` |
| `GET` | `/api/recently-played/me` | Fetch playback history | `USER` |

#### Artist Studio & Workspace

| Method | Endpoint | Description | Role / Auth |
|---|---|---|---|
| `POST` | `/api/artist-applications` | Submit application to become an artist | `USER` |
| `GET` | `/api/artist-applications/me` | Check user's artist application status | `USER` |
| `POST` | `/api/artist/songs` | Upload new song (Multipart: audio, artwork, lyrics) | `ARTIST` |
| `POST` | `/api/artist/albums` | Create new album | `ARTIST` |

#### Administrator Moderation

| Method | Endpoint | Description | Role / Auth |
|---|---|---|---|
| `GET` | `/api/admin/applications` | List pending artist applications | `ADMIN` |
| `PUT` | `/api/admin/applications/{id}` | Approve or reject artist application | `ADMIN` |
| `GET` | `/api/admin/content/pending` | List pending songs awaiting moderation | `ADMIN` |
| `PUT` | `/api/admin/content/{id}/review`| Approve or reject song publication | `ADMIN` |

### 9.3 Authentication and Authorization

- **Stateless Tokens:** Access tokens are signed using HMAC-SHA256 and expire in 15 minutes.
- **Concurrency-Safe Refresh:** The `/api/auth/refresh` endpoint accepts a secure HttpOnly cookie and returns a fresh access token without invalidating simultaneous client requests.
- **Role Hierarchy:**
  - `ROLE_USER`: Standard access to streaming, playlists, favorites, and artist application submission.
  - `ROLE_ARTIST`: Inherits `ROLE_USER` permissions plus discography CRUD and media upload studio.
  - `ROLE_ADMIN`: Platform moderation, verification of artist applications, and content approval.

### 9.4 Request and Response Examples

#### Login Request

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "minhhau",
  "password": "Password123!"
}
```

#### Login Response (`200 OK`)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "minhhau",
    "email": "minhhau@example.com",
    "role": "USER",
    "avatar": "https://res.cloudinary.com/demo/image/upload/avatar.jpg"
  }
}
```

### 9.5 API Testing Tools

- **Swagger UI:** Accessible at `http://localhost:8000/swagger-ui.html` for real-time testing.
- **cURL:** Example command to retrieve song catalog:
  ```bash
  curl -X GET "http://localhost:8000/api/songs?page=0&size=10" -H "Accept: application/json"
  ```

---

## 10. Testing

### 10.1 Testing Strategy

The backend employs a comprehensive multi-level testing methodology:
- **Unit Tests:** Focus on use case business rules, DTO mapping accuracy, and domain entity behaviors.
- **Integration Tests:** Validate Spring Data JPA repository queries, Flyway schema migrations, and REST controller endpoints using `@SpringBootTest` and `@AutoConfigureMockMvc`.
- **Security Tests:** Verify access restrictions for `USER`, `ARTIST`, and `ADMIN` roles.

### 10.2 Running Tests

Execute all tests using the Maven Wrapper:

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthControllerTest

# Run tests with test compilation check
./mvnw test-compile
```

*(On Windows PowerShell, use `.\mvnw.cmd`)*

### 10.3 Test Coverage

- Authentication workflow (register, login, token refresh, logout).
- CRUD operations for songs, albums, and playlists.
- Role-based authorization filters.
- Exception mapping and response normalization.

---

## 11. Deployment

### 11.1 Deployment Architecture

```
                       [Internet / Clients]
                                |
                                v
               +----------------------------------+
               |      Reverse Proxy (Nginx)       |
               +----------------+-----------------+
                                |
                                v
               +----------------------------------+
               |     Spring Boot REST API Node    |
               |       (Docker Container)         |
               +--------+----------------+--------+
                        |                |
             +----------+                +----------+
             |                                      |
             v                                      v
+------------------------+             +------------------------+
|   PostgreSQL 16 DB     |             |    Cloudinary CDN      |
|  (Relational Storage)  |             |  (MP3 & Image Assets)  |
+------------------------+             +------------------------+
```

### 11.2 Deployment Prerequisites

- Cloud Virtual Machine (e.g. AWS EC2, DigitalOcean Droplet, Linode) or PaaS (Render, Railway).
- Managed PostgreSQL database (e.g. Supabase, Neon, AWS RDS).
- Cloudinary credentials configured in the environment.
- Docker and Docker Compose installed on the host machine.

### 11.3 Deployment Steps (Docker)

1. Build the production Docker image:
   ```bash
   docker build -t spotify-clone-back:latest .
   ```
2. Run the container with production environment variables:
   ```bash
   docker run -d \
     -p 8000:8000 \
     --name spotify-backend \
     --env-file .env \
     spotify-clone-back:latest
   ```
3. Verify health via `curl http://localhost:8000/actuator/health`.

### 11.4 CI/CD Pipeline

The project supports automated CI workflows via GitHub Actions:
- **Continuous Integration:** Automatically runs `./mvnw clean test` on pull requests to `main`.
- **Automated Artifact Packaging:** Builds Docker image and publishes to GitHub Container Registry (GHCR) upon release.

### 11.5 Monitoring and Maintenance

- **Health Checks:** Accessible via Spring Boot Actuator at `/actuator/health`.
- **Metrics:** Exposes operational metrics at `/actuator/metrics`.
- **Database Backups:** Daily automated backups of PostgreSQL databases.

---

## 12. Roadmap

### Completed

- [x] Clean Architecture setup separating domain, application, infrastructure, and presentation.
- [x] JWT authentication with HttpOnly refresh cookie rotation.
- [x] Catalog management for songs, albums, and artists.
- [x] Standalone single song support without requiring album association (`V5`).
- [x] Cloudinary media upload integration for MP3 files and cover art.
- [x] Automated audio duration extraction from MP3 metadata.
- [x] Playlist CRUD with track ordering.
- [x] Artist onboarding and approval workflow.
- [x] Admin content review and moderation portal.
- [x] Flyway database migrations (`V1` - `V5`).

### In Progress

- [ ] Rate limiting on authentication endpoints to prevent brute-force attacks.
- [ ] Redis caching for top charts and popular artist queries.

### Planned

- [ ] HLS (HTTP Live Streaming) adaptive bitrate audio streaming.
- [ ] WebSockets for collaborative real-time listening rooms.
- [ ] Automated integration with Spotify Web API for catalog synchronization.
- [ ] Recommendation engine powered by vector embeddings.

---

## 13. Contributing

Contributions are welcomed. To ensure code quality and consistency:

1. **Fork the Repository**
2. **Create a Feature Branch:**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your Changes:**
   ```bash
   git commit -m "feat: Add support for custom audio codecs"
   ```
4. **Push to the Branch:**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request** describing your changes and testing procedures.

### Code Guidelines

- Adhere to Clean Architecture layering: controllers never access persistence entities directly.
- Ensure all business operations are covered by unit or integration tests.
- Verify that `./mvnw test` passes without errors before submitting PRs.

---

## 14. License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 15. Contact

- **Author:** Minh Hậu (Minhhau32223)
- **GitHub:** [@Minhhau32223](https://github.com/Minhhau32223)
- **Backend Repository:** [https://github.com/Minhhau32223/spotify_clone_back](https://github.com/Minhhau32223/spotify_clone_back)
- **Frontend Repository:** [https://github.com/Minhhau32223/music_front](https://github.com/Minhhau32223/music_front)

---

## Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) for the enterprise-ready application framework.
- [Cloudinary](https://cloudinary.com/) for reliable cloud media delivery and audio streaming.
- [Flyway](https://flywaydb.org/) for reproducible database schema migrations.
- The open source communities behind Java, PostgreSQL, and Spring Security.
