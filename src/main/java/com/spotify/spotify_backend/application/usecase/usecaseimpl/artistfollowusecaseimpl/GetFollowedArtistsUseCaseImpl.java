package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistfollowusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.usecase.artistfollowusecase.GetFollowedArtistsUseCase;
import com.spotify.spotify_backend.domain.repository.ArtistFollowRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetFollowedArtistsUseCaseImpl
        implements GetFollowedArtistsUseCase {

    private final ArtistFollowRepository
            artistFollowRepository;

    private final ArtistRepository
            artistRepository;

    @Override
    public List<ArtistResponse> execute(
            Long userId
    ) {

        return artistFollowRepository
                .findByUserId(userId)
                .stream()
                .map(item ->
                        artistRepository
                                .findById(
                                        item.getArtistId()
                                )
                                .orElseThrow()
                )
                .map(ArtistMapper::toResponse)
                .toList();
    }
}