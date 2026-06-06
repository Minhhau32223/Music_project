package com.spotify.spotify_backend.infrastructure.persistence.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;

public interface SongJpaRepository extends JpaRepository<SongEntity, Long> {
    Optional<SongEntity> findById(Long id);
    List<SongEntity> findByAlbumId(Long albumId);
}
