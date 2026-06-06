package com.spotify.spotify_backend.application.usecase.albumusecase;

import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;

public interface GetAlbumUseCase {

    AlbumResponse execute(Long id);

}