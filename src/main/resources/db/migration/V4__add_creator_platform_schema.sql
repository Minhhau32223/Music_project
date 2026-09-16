ALTER TABLE artists ADD COLUMN user_id BIGINT UNIQUE;

ALTER TABLE albums
    ADD COLUMN artist_id BIGINT,
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
    ADD COLUMN rejection_reason TEXT,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN published_at TIMESTAMP;

ALTER TABLE songs
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
    ADD COLUMN rejection_reason TEXT,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN published_at TIMESTAMP;

CREATE TABLE artist_applications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    artist_name VARCHAR(255) NOT NULL,
    bio TEXT NOT NULL,
    profile_image VARCHAR(2048) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT,
    rejection_reason TEXT
);

CREATE INDEX idx_artist_applications_user_id ON artist_applications(user_id);
CREATE INDEX idx_artist_applications_status ON artist_applications(status);
CREATE INDEX idx_songs_status ON songs(status);
CREATE INDEX idx_songs_artist_status ON songs(artist_id, status);
CREATE INDEX idx_albums_artist_id ON albums(artist_id);
CREATE INDEX idx_albums_status ON albums(status);
