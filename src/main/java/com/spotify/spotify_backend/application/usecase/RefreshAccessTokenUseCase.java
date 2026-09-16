package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.application.dto.Response.AccessTokenResponse;

public interface RefreshAccessTokenUseCase {
    AccessTokenResponse execute(String refreshToken);
}
