package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.Artist;

public interface ArtistRepository {
    Artist save(Artist artist);
    Optional<Artist> findById(Long id);
    List<Artist> findAll();
    void deleteById(Long id);
    List<Artist> findByNameContaining(String keyword);
}
