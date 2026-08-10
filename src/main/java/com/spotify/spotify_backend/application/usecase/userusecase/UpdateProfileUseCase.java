package com.spotify.spotify_backend.application.usecase.userusecase;

import com.spotify.spotify_backend.application.dto.Request.UpdateProfileRequest;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;

public interface UpdateProfileUseCase {
 UserProfileResponse execute(
            Long userId,
            UpdateProfileRequest request
    );

}
