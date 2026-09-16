package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.Album;
public interface AlbumRepository {
    Album save(Album album);
    Optional<Album> findById(Long id);
    List<Album> findAll();
    void deleteById(Long id);
    List<Album> findByNameContaining(
        String keyword
);
    Optional<Album> findPublishedById(Long id);
    List<Album> findAllPublished();
    List<Album> findPublishedByNameContaining(String keyword);
}
