package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.application.dto.Request.UploadSongRequest;
import com.spotify.spotify_backend.application.dto.Response.UploadSongResponse;

public interface UploadSongUseCase {
    UploadSongResponse execute(UploadSongRequest request);
}
