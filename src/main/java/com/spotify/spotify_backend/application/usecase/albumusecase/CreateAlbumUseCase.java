package com.spotify.spotify_backend.application.usecase.albumusecase;

import com.spotify.spotify_backend.application.dto.Request.CreateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;

public interface CreateAlbumUseCase {

    AlbumResponse execute(CreateAlbumRequest request);

}