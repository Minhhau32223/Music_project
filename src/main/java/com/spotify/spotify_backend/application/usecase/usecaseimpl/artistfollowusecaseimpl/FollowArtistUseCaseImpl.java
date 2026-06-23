package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistfollowusecaseimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.artistfollowusecase.FollowArtistUseCase;
import com.spotify.spotify_backend.domain.model.ArtistFollow;
import com.spotify.spotify_backend.domain.repository.ArtistFollowRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowArtistUseCaseImpl
        implements FollowArtistUseCase {

    private final ArtistRepository
            artistRepository;

    private final ArtistFollowRepository
            artistFollowRepository;

    @Override
    public void execute(
            Long userId,
            Long artistId
    ) {

        artistRepository
                .findById(artistId)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Artist not found"
                                )
                );

        boolean exists =
                artistFollowRepository
                        .findByUserIdAndArtistId(
                                userId,
                                artistId
                        )
                        .isPresent();

        if (exists) {

            throw new RuntimeException(
                    "Already followed"
            );
        }

        ArtistFollow follow =
                ArtistFollow.builder()
                        .userId(userId)
                        .artistId(artistId)
                        .followedAt(
                                LocalDateTime.now()
                        )
                        .build();

        artistFollowRepository.save(
                follow
        );
    }
}