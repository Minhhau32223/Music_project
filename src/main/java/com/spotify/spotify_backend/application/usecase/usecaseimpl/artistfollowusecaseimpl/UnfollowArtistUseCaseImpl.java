package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistfollowusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.artistfollowusecase.UnfollowArtistUseCase;
import com.spotify.spotify_backend.domain.repository.ArtistFollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnfollowArtistUseCaseImpl
        implements UnfollowArtistUseCase {

    private final ArtistFollowRepository
            artistFollowRepository;

    @Override
    public void execute(
            Long userId,
            Long artistId
    ) {

        artistFollowRepository
                .deleteByUserIdAndArtistId(
                        userId,
                        artistId
                );
    }
}