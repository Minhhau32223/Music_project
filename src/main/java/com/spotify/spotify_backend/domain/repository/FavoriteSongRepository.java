package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.FavoriteSong;

public interface FavoriteSongRepository {

    FavoriteSong save(
            FavoriteSong favoriteSong
    );

    Optional<FavoriteSong> findByUserIdAndSongId(
            Long userId,
            Long songId
    );

    List<FavoriteSong> findByUserId(
            Long userId
    );

    void deleteByUserIdAndSongId(
            Long userId,
            Long songId
    );
}