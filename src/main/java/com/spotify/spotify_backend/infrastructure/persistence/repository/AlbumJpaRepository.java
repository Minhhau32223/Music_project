package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;

public interface AlbumJpaRepository extends JpaRepository<AlbumEntity, Long> {
    Optional<AlbumEntity> findById(Long id);
    Optional<AlbumEntity> findByName(String name);
    void deleteByName(String name);
    List<AlbumEntity> findByNameContainingIgnoreCase(String keyword);
    Optional<AlbumEntity> findByIdAndStatus(Long id, String status);
    List<AlbumEntity> findByStatus(String status);
    List<AlbumEntity> findByNameContainingIgnoreCaseAndStatus(String keyword, String status);
    List<AlbumEntity> findByArtistIdOrderByCreatedAtDesc(Long artistId);
    List<AlbumEntity> findByStatusOrderByCreatedAtAsc(String status);
    
}
