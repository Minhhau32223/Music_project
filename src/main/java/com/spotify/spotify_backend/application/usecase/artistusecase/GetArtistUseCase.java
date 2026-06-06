package com.spotify.spotify_backend.application.usecase.artistusecase;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;

public interface GetArtistUseCase {

    ArtistResponse execute(Long id);

}