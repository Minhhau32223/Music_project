package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.PageRequest;

import com.spotify.spotify_backend.domain.model.RecentlyPlayed;
import com.spotify.spotify_backend.domain.repository.RecentlyPlayedRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.RecentlyPlayedEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.RecentlyPlayedJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecentlyPlayedRepositoryImpl
        implements RecentlyPlayedRepository {

    private final RecentlyPlayedJpaRepository
            repository;

    @Override
    public RecentlyPlayed save(
            RecentlyPlayed history
    ) {

        RecentlyPlayedEntity entity =
                RecentlyPlayedEntity.builder()
                        .userId(
                                history.getUserId()
                        )
                        .songId(
                                history.getSongId()
                        )
                        .playedAt(
                                history.getPlayedAt()
                        )
                        .build();

        return mapToDomain(
                repository.save(entity)
        );
    }

    @Override
    public List<RecentlyPlayed> findByUserId(
            Long userId
    ) {

        return repository
                .findByUserIdOrderByPlayedAtDesc(
                        userId
                )
                .stream()
                .map(this::mapToDomain)
                .toList();
    }
    @Override
public long countByUserId(
        Long userId
) {
    return repository.countByUserId(
            userId
    );
}

@Override
public void deleteOldestByUserId(
        Long userId
) {
    repository.deleteOldestByUserId(
            userId
    );
}

    @Override
    public List<RecentlyPlayed> findRecentByUserId(Long userId, int limit) {
        return repository.findByUserIdOrderByPlayedAtDescIdDesc(userId,
                PageRequest.of(0, limit))
                .stream().map(this::mapToDomain).toList();
    }

    private RecentlyPlayed mapToDomain(
            RecentlyPlayedEntity entity
    ) {

        return RecentlyPlayed.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .songId(entity.getSongId())
                .playedAt(entity.getPlayedAt())
                .build();
    }
}
