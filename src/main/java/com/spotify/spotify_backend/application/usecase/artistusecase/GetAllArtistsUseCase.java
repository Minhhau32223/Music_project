package com.spotify.spotify_backend.application.usecase.artistusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;

public interface GetAllArtistsUseCase {

    List<ArtistResponse> execute();

}