package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;

public interface AlbumJpaRepository extends JpaRepository<AlbumEntity, Long> {
    Optional<AlbumEntity> findById(Long id);
    Optional<AlbumEntity> findByName(String name);
    void deleteByName(String name);
    
}
