package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.ArtistFollow;

public interface ArtistFollowRepository {

    ArtistFollow save(
            ArtistFollow artistFollow
    );

    Optional<ArtistFollow>
    findByUserIdAndArtistId(
            Long userId,
            Long artistId
    );

    List<ArtistFollow>
    findByUserId(
            Long userId
    );

    void deleteByUserIdAndArtistId(
            Long userId,
            Long artistId
    );
}