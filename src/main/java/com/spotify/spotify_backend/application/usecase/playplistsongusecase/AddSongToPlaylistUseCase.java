package com.spotify.spotify_backend.application.usecase.playplistsongusecase;
import com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest;

public interface AddSongToPlaylistUseCase {
    void execute(
            Long playlistId,
            AddSongToPlaylistRequest request
    );
}
