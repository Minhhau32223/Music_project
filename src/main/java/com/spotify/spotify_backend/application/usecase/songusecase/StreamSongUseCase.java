package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.application.dto.Response.StreamingResponse;

public interface StreamSongUseCase {

    StreamingResponse execute(Long songId);

}
