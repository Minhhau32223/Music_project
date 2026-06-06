package com.spotify.spotify_backend.application.usecase.artistusecase;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;

public interface CreateArtistUseCase {

    ArtistResponse execute(CreateArtistRequest request);

}