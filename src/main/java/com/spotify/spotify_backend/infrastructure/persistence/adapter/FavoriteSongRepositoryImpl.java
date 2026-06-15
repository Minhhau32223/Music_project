package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.FavoriteSong;
import com.spotify.spotify_backend.domain.repository.FavoriteSongRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.FavoriteSongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.FavoriteSongJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FavoriteSongRepositoryImpl
        implements FavoriteSongRepository {

    private final FavoriteSongJpaRepository
            favoriteSongJpaRepository;

    @Override
    public FavoriteSong save(
            FavoriteSong favoriteSong
    ) {

        FavoriteSongEntity entity =
                FavoriteSongEntity.builder()
                        .id(favoriteSong.getId())
                        .userId(favoriteSong.getUserId())
                        .songId(favoriteSong.getSongId())
                        .createdAt(favoriteSong.getCreatedAt())
                        .build();

        FavoriteSongEntity saved =
                favoriteSongJpaRepository.save(entity);

        return mapToDomain(saved);
    }

    @Override
    public Optional<FavoriteSong> findByUserIdAndSongId(
            Long userId,
            Long songId
    ) {

        return favoriteSongJpaRepository
                .findByUserIdAndSongId(
                        userId,
                        songId
                )
                .map(this::mapToDomain);
    }

    @Override
    public List<FavoriteSong> findByUserId(
            Long userId
    ) {

        return favoriteSongJpaRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void deleteByUserIdAndSongId(
            Long userId,
            Long songId
    ) {

        favoriteSongJpaRepository
                .deleteByUserIdAndSongId(
                        userId,
                        songId
                );
    }

    private FavoriteSong mapToDomain(
            FavoriteSongEntity entity
    ) {

        return FavoriteSong.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .songId(entity.getSongId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}