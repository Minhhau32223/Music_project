package com.spotify.spotify_backend.application.usecase.userusecase;

import com.spotify.spotify_backend.application.dto.Request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void execute(Long userId, ChangePasswordRequest request);
}
