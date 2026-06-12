package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistEntity;

public interface PlaylistJpaRepository
        extends JpaRepository<PlaylistEntity, Long> {

    List<PlaylistEntity> findByUserId(Long userId);
}