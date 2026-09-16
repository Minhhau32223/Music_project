# Development seed (no songs/media)

The `DevDataSeeder` runs only with the `dev` profile and `APP_SEED_ENABLED=true`. It is idempotent and never runs in production.

It creates ADMIN, active/disabled USER accounts, two ARTIST accounts with artist profiles, pending/rejected artist applications, albums in every lifecycle state, empty playlists and artist follows. **It intentionally creates no song, media URL, favorite, playlist-song or listening-history record.** Add songs and media through the Artist/Admin APIs afterwards.

Set `DEV_SEED_PASSWORD` locally before starting the app. This value is only for local demo accounts and is never committed.

## pgAdmin 4

After starting Docker Compose, register a server with host `localhost`, port `${POSTGRES_PORT}` (default `5432`), database `music_app`, and the `DB_USERNAME` / `DB_PASSWORD` values from your local `.env`.
