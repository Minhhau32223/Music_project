package com.spotify.spotify_backend.application.usecase.playlistusecase;

import com.spotify.spotify_backend.application.dto.Request.UpdatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;

public interface UpdatePlaylistUseCase {
    PlaylistResponse execute(Long userId, Long playlistId, UpdatePlaylistRequest request);
}
