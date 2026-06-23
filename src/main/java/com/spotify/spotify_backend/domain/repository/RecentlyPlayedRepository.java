package com.spotify.spotify_backend.domain.repository;

import java.util.List;

import com.spotify.spotify_backend.domain.model.RecentlyPlayed;

public interface RecentlyPlayedRepository {

    RecentlyPlayed save(
            RecentlyPlayed recentlyPlayed
    );

    List<RecentlyPlayed> findByUserId(
            Long userId
    );

    long countByUserId(
            Long userId
    );

    void deleteOldestByUserId(
            Long userId
    );
}