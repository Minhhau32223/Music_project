package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.ArtistFollow;
import com.spotify.spotify_backend.domain.repository.ArtistFollowRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistFollowEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistFollowJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArtistFollowRepositoryImpl
        implements ArtistFollowRepository {

    private final ArtistFollowJpaRepository
            artistFollowJpaRepository;

    @Override
    public ArtistFollow save(
            ArtistFollow artistFollow
    ) {

        ArtistFollowEntity entity =
                ArtistFollowEntity.builder()
                        .id(artistFollow.getId())
                        .userId(
                                artistFollow.getUserId()
                        )
                        .artistId(
                                artistFollow.getArtistId()
                        )
                        .followedAt(
                                artistFollow.getFollowedAt()
                        )
                        .build();

        ArtistFollowEntity saved =
                artistFollowJpaRepository
                        .save(entity);

        return mapToDomain(saved);
    }

    @Override
    public Optional<ArtistFollow>
    findByUserIdAndArtistId(
            Long userId,
            Long artistId
    ) {

        return artistFollowJpaRepository
                .findByUserIdAndArtistId(
                        userId,
                        artistId
                )
                .map(this::mapToDomain);
    }

    @Override
    public List<ArtistFollow>
    findByUserId(
            Long userId
    ) {

        return artistFollowJpaRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void deleteByUserIdAndArtistId(
            Long userId,
            Long artistId
    ) {

        artistFollowJpaRepository
                .deleteByUserIdAndArtistId(
                        userId,
                        artistId
                );
    }

    private ArtistFollow mapToDomain(
            ArtistFollowEntity entity
    ) {

        return ArtistFollow.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .artistId(entity.getArtistId())
                .followedAt(entity.getFollowedAt())
                .build();
    }
}