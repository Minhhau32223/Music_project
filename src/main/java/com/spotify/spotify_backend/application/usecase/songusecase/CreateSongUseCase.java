package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.application.dto.Request.CreateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface CreateSongUseCase {
    SongResponse execute(CreateSongRequest request);
}
