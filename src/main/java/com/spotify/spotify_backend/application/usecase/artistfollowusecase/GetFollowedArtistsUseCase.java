package com.spotify.spotify_backend.application.usecase.artistfollowusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;

public interface GetFollowedArtistsUseCase {
    List<ArtistResponse> execute(
            Long userId
    );
}
