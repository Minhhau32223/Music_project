package com.spotify.spotify_backend.application.usecase.albumusecase;

import com.spotify.spotify_backend.application.dto.Request.UpdateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;

public interface UpdateAlbumUseCase {
    AlbumResponse execute(Long albumId, UpdateAlbumRequest request);
}
