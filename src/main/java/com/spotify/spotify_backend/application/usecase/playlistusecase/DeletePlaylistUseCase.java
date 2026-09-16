package com.spotify.spotify_backend.application.usecase.playlistusecase;

public interface DeletePlaylistUseCase {
    void execute(Long userId, Long id);
}
