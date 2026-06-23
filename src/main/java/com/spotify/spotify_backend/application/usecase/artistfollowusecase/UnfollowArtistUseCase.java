package com.spotify.spotify_backend.application.usecase.artistfollowusecase;

public interface UnfollowArtistUseCase {
    void execute(
            Long userId,
            Long artistId
    );
}
