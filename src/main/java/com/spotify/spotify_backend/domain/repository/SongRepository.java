package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.Song;
public interface SongRepository {
    Song save(Song song);
    Optional<Song> findById(Long id);
    List<Song> findAll();
    List<Song> findByAlbumId(Long albumId);
    void deleteById(Long id);
    List<Song> findByNameContaining(String keyword);
}
