package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistSongEntity;

public interface PlaylistSongJpaRepository
        extends JpaRepository<PlaylistSongEntity, Long> {

    List<PlaylistSongEntity>
    findByPlaylistId(Long playlistId);

    Optional<PlaylistSongEntity>
    findByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    );

    void deleteByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    );
}