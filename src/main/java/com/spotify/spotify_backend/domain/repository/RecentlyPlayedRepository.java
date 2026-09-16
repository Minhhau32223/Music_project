package com.spotify.spotify_backend.domain.repository;

import java.util.List;

import com.spotify.spotify_backend.domain.model.RecentlyPlayed;

public interface RecentlyPlayedRepository {
    List<RecentlyPlayed> findRecentByUserId(Long userId, int limit);

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
