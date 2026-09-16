# API Inventory — Synchronization Phase

**Snapshot:** 2026-09-13  
**Source of truth:** running backend OpenAPI at `/v3/api-docs`, controllers, and the frontend API modules.  
**Runtime verified:** Docker frontend `http://127.0.0.1:5173` → `/api` → Docker backend `http://127.0.0.1:8000` → PostgreSQL Docker.

## Contract decision

- The canonical prefix is `/api`.
- The backend's existing DTOs are the transport contract. Frontend maps them once in feature API mappers; pages consume frontend domain types.
- Successful responses remain endpoint-specific (for example arrays, resource objects, and paginated results). A global `{ success, message, data }` envelope is **not** introduced during synchronization because it would be a breaking, cross-cutting API migration without a product need.
- Auth uses a bearer access token plus the refresh-token cookie. `apiClient` owns bearer injection, one refresh-and-retry, and normalized errors.
- Slash variants are retained where the backend supports both. The frontend calls the exact canonical variants below.

## Backend API inventory and frontend usage

| Module | Method | Canonical endpoint | Auth | Frontend consumer | Status |
| --- | --- | --- | --- | --- | --- |
| Auth | POST | `/api/auth/register/` | Public | `auth.api.register` | DONE |
| Auth | POST | `/api/auth/login/` | Public | `auth.api.login` | DONE |
| Auth | POST | `/api/auth/refresh/` | Refresh cookie | `apiClient` interceptor | DONE |
| Auth | POST | `/api/auth/logout/` | Refresh cookie | `auth.api.logout` | DONE |
| User | GET | `/api/users/me/` | User | `auth.api.currentUser` | DONE |
| Catalog | GET | `/api/songs/filter` | Public | `songApi.getSongs` | DONE |
| Catalog | GET | `/api/songs/{id}/` | Public | `songApi.getSong` | DONE |
| Catalog | GET | `/api/artists/` | Public | `artistApi.getArtists` | DONE |
| Catalog | GET | `/api/artists/{id}/` | Public | `artistApi.getArtist` | DONE |
| Catalog | GET | `/api/albums/` | Public | `albumApi.getAlbums` | DONE |
| Catalog | GET | `/api/albums/{id}/` | Public | `albumApi.getAlbum` | DONE |
| Catalog | GET | `/api/albums/{id}/songs/` | Public | `albumApi.getAlbumTracks` | DONE |
| Discovery | GET | `/api/recommendations/top-songs?limit=` | Public | `songApi.getPopularSongs` | DONE |
| Discovery | GET | `/api/recommendations/new-releases?limit=` | Public | `songApi.getNewReleaseSongs`, `albumApi.getNewReleases` | DONE |
| Discovery | GET | `/api/recommendations/personalized?limit=` | User | `songApi.getPersonalizedSongs` | DONE |
| Search | GET | `/api/search?keyword=` | Public | `searchApi.search` | DONE |
| Favorites | GET | `/api/favorites/me` | User | `favoriteApi.getLikedSongs` | DONE |
| Favorites | POST/DELETE | `/api/favorites/{songId}` | User | `favoriteApi.likeSong/unlikeSong` | DONE |
| Artist follows | GET | `/api/artist-follows/me/` | User | `libraryApi.getFollowedArtists` | DONE |
| Artist follows | POST/DELETE | `/api/artist-follows/{artistId}/` | User | `libraryApi.follow/unfollowArtist` | DONE |
| Playlists | GET | `/api/playlists/` | User | `playlistApi.getPlaylists` | DONE |
| Playlists | POST | `/api/playlists/create` | User | `playlistApi.createPlaylist` | DONE |
| Playlists | GET/PUT/DELETE | `/api/playlists/{id}` | User/owner | `playlistApi` | DONE |
| Playlist songs | GET/POST/DELETE | `/api/playlists/{playlistId}/songs` | User/owner | `playlistApi` | DONE |

## Backend APIs not consumed by the current user frontend

| Module | Endpoint group | Status | Reason |
| --- | --- | --- | --- |
| Streaming | `/api/stream/song/{id}`, `/play` | NOT_USED | Player consumes the Cloudinary `audioUrl` provided by the song DTO. |
| Recently played | `/api/recently-played/**` | PARTIAL | Backend exists; no frontend integration yet. |
| User profile | `/api/users/profile/`, `/avatar/`, `/change-password/`, DELETE `/me/` | NOT_USED | Profile settings UI is not in the current core flow. |
| Catalog mutation | create/update/delete/upload under songs, albums, artists | NOT_USED | No creator/admin UI in the user frontend. |
| Artist applications/admin moderation | `/api/artist-applications/**`, `/api/admin/artist-applications/**` | NOT_USED | Deferred to the artist/admin phase. |

## Gap analysis

| Category | Finding | Resolution / status |
| --- | --- | --- |
| Base URL | Native development needs a direct backend origin; Docker frontend needs same-origin `/api`. | RESOLVED: `VITE_API_BASE_URL=/api` is supplied to the Docker build; nginx proxies `/api` to `backend:8000`. |
| Database target | Native backend defaulted to a host PostgreSQL database with no catalog records; the Docker volume contains 11 songs. | RESOLVED for the active runtime: full Docker stack uses `postgres_db` and `postgres_data`. |
| Search parameter | The backend accepts `keyword`, not `q`. | RESOLVED: frontend sends `keyword`. |
| Search scope | Backend response contains songs, albums, artists; it does not include playlists. | ALIGNED: UI does not fabricate playlist results. |
| Response shapes | Lists are raw arrays, catalog filtering is paginated, and resource endpoints are objects. | ACCEPTED: feature-level mappers isolate each shape; no breaking global envelope migration. |
| Mock data | Legacy mock modules remain for the UI playground and unused legacy pages. | PARTIAL: core router (`/home`, search, details, library) uses live API modules. Remove unused legacy pages before a production release. |
| Dev architecture | The roadmap recommends only Dockerizing infrastructure, but the current active stack is Docker frontend + backend + infrastructure, by the earlier runtime decision. | DECISION REQUIRED before changing: keep the working full Docker stack, or explicitly switch to native Vite/Spring plus Docker Postgres/Redis. |

## Verification evidence

- `GET http://127.0.0.1:8000/actuator/health` → `200`.
- `GET http://127.0.0.1:5173/health` → `200`.
- `GET http://127.0.0.1:8000/api/songs/filter?page=0&size=20` → `totalElements: 11`.
- `GET http://127.0.0.1:5173/api/songs/filter?page=0&size=20` → `totalElements: 11`.

## Synchronization gates remaining

1. Run authenticated browser smoke tests: register/login/logout, refresh retry, favorites, playlist mutations, and authorization errors.
2. Decide the intended development runtime architecture noted above; do not maintain both native and Docker application servers on the same ports.
3. Remove or quarantine legacy mock-only pages after confirming no router entry imports them.
4. Integrate recently played only if it is included in the next product phase; otherwise retain it as an intentionally unused backend capability.
