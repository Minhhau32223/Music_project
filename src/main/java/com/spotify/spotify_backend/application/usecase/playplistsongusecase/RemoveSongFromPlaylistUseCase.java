package com.spotify.spotify_backend.application.usecase.playplistsongusecase;

public interface RemoveSongFromPlaylistUseCase {

    void execute(
            Long userId,
            Long playlistId,
            Long songId
    );
}
