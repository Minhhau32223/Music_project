# Backend API inventory — SYNC-1

Source: controllers and `SecurityConfig` on 2026-09-13. `GET` catalog queries only expose `PUBLISHED` content.

| Module | Method | Endpoint | Auth / role | Request | Response | Status |
|---|---|---|---|---|---|---|
| Auth | POST | `/api/auth/register/` | Public | `RegisterRequest` | `RegisterResponse` | DONE |
| Auth | POST | `/api/auth/login/` | Public | `LoginRequest` | `LoginResponse` + refresh cookie | DONE |
| Auth | POST | `/api/auth/refresh/` | Refresh cookie | none | `AccessTokenResponse` | DONE |
| Auth | POST | `/api/auth/logout/` | Public cookie clear | none | 204 | DONE |
| User | GET | `/api/users/me/` | JWT | none | `UserProfileResponse` | DONE |
| User | PUT | `/api/users/profile/`, `/avatar/`, `/change-password/` | JWT | JSON/multipart | profile / 204 | DONE |
| Catalog | GET | `/api/songs/filter` | Public | artistId, albumId, sortBy, sortDirection, page, size | `PageResponse<SongResponse>` | DONE |
| Catalog | GET | `/api/songs/{id}/`, `/albums/**`, `/artists/**` | Public | path | DTO lists/details | DONE |
| Discovery | GET | `/api/search?keyword=` | Public | keyword | `SearchResponse` | DONE |
| Discovery | GET | `/api/recommendations/top-songs`, `/new-releases` | Public | limit | `SongResponse[]` | DONE |
| Discovery | GET | `/api/recommendations/personalized` | JWT | limit | `SongResponse[]` | DONE |
| Player | GET | `/api/stream/song/{id}`, `/play` | Public | path | stream JSON / 302 Cloudinary URL | DONE |
| Library | `/api/favorites/me`, `/{songId}` | JWT | path | songs / 204 | DONE |
| Library | `/api/playlists/**` | Public read; JWT mutation + ownership | JSON/multipart | playlist DTO | DONE |
| Library | `/api/artist-follows/me`, `/{artistId}` | JWT | path | artists / 204 | DONE |
| Library | `/api/recently-played/me`, `/play` | JWT | playback event | history / 204 | DONE |
| Artist | `/api/artist-applications`, `/me` | JWT | application JSON | application DTO | DONE |
| Artist | `/api/artist/**` | `ROLE_ARTIST` + ownership | JSON/multipart | creator content DTO | DONE |
| Admin | `/api/admin/artist-applications/**`, `/api/admin/songs/**`, `/api/admin/albums/**` | `ROLE_ADMIN` | moderation JSON | moderation DTO | DONE |

Errors use `timestamp`, `status`, `error`, `message`, `path`, and optional `errors` field map. This is the V1 error contract.
