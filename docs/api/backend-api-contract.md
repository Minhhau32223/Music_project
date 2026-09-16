# NOCTURNE backend API contract — Phase 7 audit

Snapshot date: 2026-09-12  
Runtime base URL: `http://127.0.0.1:8000/api`  
Machine-readable snapshot: `backend-api-contract.json`

## Search

`GET /search?keyword={text}` is public.

- Required backend parameter: `keyword`
- Frontend canonical parameter: `/search?q={text}`
- Frontend adapter maps `q` to `keyword`
- Response groups: `songs`, `albums`, `artists`
- Unsupported by this endpoint: playlists, pagination, entity type filters, result limit
- No relevance score or ranking contract exists, so the frontend intentionally omits a synthetic “Top Result”
- Public songs and albums are restricted to `PUBLISHED`

## Discovery and recommendations

- `GET /recommendations/top-songs?limit={1..100}` — public, returns `SongResponse[]`
- `GET /recommendations/new-releases?limit={1..100}` — public, returns `SongResponse[]`
- `GET /recommendations/personalized?limit={1..100}` — JWT required, returns `SongResponse[]`
- All recommendation queries are restricted to `PUBLISHED` songs
- Personalized cache/query identity is scoped by the authenticated frontend user id

## Catalog filter

`GET /songs/filter` is public and returns a paged song response.

- Optional filters: `artistId`, `albumId`
- Sort whitelist: `playCount`, `id`, `createdAt`, `name`
- Direction: `ASC` or `DESC`
- `page >= 0`; `size` is from 1 through 100
- Results are restricted to `PUBLISHED` songs

## Playback

- `GET /stream/song/{id}` returns streaming metadata
- `GET /stream/song/{id}/play` redirects to the playable media URL
- Public streaming resolves only `PUBLISHED` songs
- The frontend player uses a `playbackUrl` abstraction; current `SongResponse.file` is adapted at the catalog boundary

## Auth and private discovery

- Current user: `GET /users/me/`
- Supported roles: `USER`, `ARTIST`, `ADMIN`
- Logout clears TanStack Query private caches and local recent-search history

## Phase 7 frontend policy

- Input is trimmed but internal characters are preserved
- Search starts at two characters and is debounced by 350 ms
- TanStack Query owns cancellation via `AbortSignal`, stable normalized query keys, and a 45-second search stale time
- HTTP 4xx responses, including 429, are not automatically retried
- Recent searches are case-insensitively deduplicated and capped at eight entries
- Empty query discovery uses only the audited top-songs, new-releases, and personalized endpoints
