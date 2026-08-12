package com.spotify.spotify_backend.application.usecase.recentlyplayedusecase;

public interface RecordPlaybackUseCase {
    void execute(Long userId, Long songId);
}
