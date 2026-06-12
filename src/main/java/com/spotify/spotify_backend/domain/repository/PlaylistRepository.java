package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.Playlist;

public interface PlaylistRepository {
    Playlist save(Playlist playlist);

    Optional<Playlist> findById(Long id);

    List<Playlist> findAll();

    List<Playlist> findByUserId(Long userId);

    void deleteById(Long id);
}
