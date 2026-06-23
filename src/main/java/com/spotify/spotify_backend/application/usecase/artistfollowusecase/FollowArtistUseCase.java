package com.spotify.spotify_backend.application.usecase.artistfollowusecase;

public interface FollowArtistUseCase {
    void execute(
            Long userId,
            Long artistId
    );
}
