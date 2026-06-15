package com.spotify.spotify_backend.application.usecase.playplistsongusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface GetPlaylistSongsUseCase {

    List<SongResponse> execute(
            Long playlistId
    );
}