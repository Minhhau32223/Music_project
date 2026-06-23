package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistFollowEntity;

public interface ArtistFollowJpaRepository
        extends JpaRepository<
                ArtistFollowEntity,
                Long> {

    Optional<ArtistFollowEntity>
    findByUserIdAndArtistId(
            Long userId,
            Long artistId
    );

    List<ArtistFollowEntity>
    findByUserId(
            Long userId
    );

    void deleteByUserIdAndArtistId(
            Long userId,
            Long artistId
    );
}