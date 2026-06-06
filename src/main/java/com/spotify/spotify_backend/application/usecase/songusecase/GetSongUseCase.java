package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface GetSongUseCase {
    SongResponse execute(Long id);
}
