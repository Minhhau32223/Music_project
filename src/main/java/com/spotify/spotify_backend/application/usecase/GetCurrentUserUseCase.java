package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;

public interface GetCurrentUserUseCase {

    UserProfileResponse execute(Long userId);

}