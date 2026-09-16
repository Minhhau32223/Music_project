-- Historical creation times are unknown: retain existing values and backfill only NULLs.
ALTER TABLE songs ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
UPDATE songs SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
ALTER TABLE songs ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE songs ALTER COLUMN created_at SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_songs_artist_album ON songs (artist_id, album_id);
CREATE INDEX IF NOT EXISTS idx_songs_album ON songs (album_id);
CREATE INDEX IF NOT EXISTS idx_songs_top ON songs (play_count DESC, id ASC);
CREATE INDEX IF NOT EXISTS idx_songs_new ON songs (created_at DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_recently_played_user_time ON recently_played (user_id, played_at DESC);
CREATE INDEX IF NOT EXISTS idx_favorite_songs_user ON favorite_songs (user_id);
