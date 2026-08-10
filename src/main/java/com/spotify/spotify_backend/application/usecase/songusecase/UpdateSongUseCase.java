package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.application.dto.Request.UpdateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface UpdateSongUseCase {
SongResponse execute(

            Long songId,

            UpdateSongRequest request

    );
}
