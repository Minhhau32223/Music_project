package com.spotify.spotify_backend.application.usecase.farvoritesongusecase;

public interface AddFavoriteSongUseCase {
  void execute(
            Long userId,
            Long songId
    );
}
