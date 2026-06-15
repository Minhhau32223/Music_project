package com.spotify.spotify_backend.application.usecase.farvoritesongusecase;

public interface RemoveFavoriteSongUseCase {
void execute(
            Long userId,
            Long songId
    );
}
