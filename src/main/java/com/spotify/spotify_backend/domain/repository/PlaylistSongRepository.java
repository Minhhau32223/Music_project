package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.PlaylistSong;

public interface PlaylistSongRepository {

    PlaylistSong save(PlaylistSong playlistSong);

    List<PlaylistSong> findByPlaylistId(Long playlistId);

    Optional<PlaylistSong> findByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    );

    void deleteByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    );
}