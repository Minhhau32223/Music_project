package com.spotify.spotify_backend.application.usecase.recentlyplayedusecase;

public interface RecordPlaySongUseCase {
void execute(
            Long userId,
            Long songId
    );
}
