package com.spotify.spotify_backend.infrastructure.persistence.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;

public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, Long> { 
    Optional<ArtistEntity> findByName(String name);
    Optional<ArtistEntity> findByUserId(Long userId);
    void deleteByName(String name);
    List<ArtistEntity> findByNameContainingIgnoreCase(
        String keyword
);
}
