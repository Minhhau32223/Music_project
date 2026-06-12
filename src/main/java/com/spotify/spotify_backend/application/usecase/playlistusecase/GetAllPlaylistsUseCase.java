package com.spotify.spotify_backend.application.usecase.playlistusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;

public interface GetAllPlaylistsUseCase {
    List<PlaylistResponse> execute();
}
