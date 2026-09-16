package com.spotify.spotify_backend.application.usecase.playlistusecase;

import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;

public interface  CreatePlaylistUseCase {
    PlaylistResponse execute(
            Long userId,
            CreatePlaylistRequest request
    );
}
