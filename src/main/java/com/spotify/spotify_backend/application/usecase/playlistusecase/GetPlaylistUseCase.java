package com.spotify.spotify_backend.application.usecase.playlistusecase;

import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;

public interface  GetPlaylistUseCase {
    PlaylistResponse execute(Long id);
}
