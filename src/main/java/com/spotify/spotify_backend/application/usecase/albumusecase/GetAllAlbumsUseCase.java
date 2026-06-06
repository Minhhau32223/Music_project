package com.spotify.spotify_backend.application.usecase.albumusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;

public interface GetAllAlbumsUseCase {

    List<AlbumResponse> execute();

}