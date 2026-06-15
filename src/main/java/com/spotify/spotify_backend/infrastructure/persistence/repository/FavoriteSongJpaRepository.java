package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.FavoriteSongEntity;

public interface FavoriteSongJpaRepository
        extends JpaRepository<FavoriteSongEntity, Long> {

    Optional<FavoriteSongEntity>
    findByUserIdAndSongId(
            Long userId,
            Long songId
    );

    List<FavoriteSongEntity>
    findByUserId(
            Long userId
    );

    void deleteByUserIdAndSongId(
            Long userId,
            Long songId
    );
}