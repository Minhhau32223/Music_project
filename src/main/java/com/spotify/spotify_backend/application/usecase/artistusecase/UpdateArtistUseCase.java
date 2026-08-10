package com.spotify.spotify_backend.application.usecase.artistusecase;

import com.spotify.spotify_backend.application.dto.Request.UpdateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;

public interface UpdateArtistUseCase {
    ArtistResponse execute(Long artistId, UpdateArtistRequest request);
}
