package com.spotify.spotify_backend.application.usecase;
import com.spotify.spotify_backend.application.dto.Request.LoginRequest;
import com.spotify.spotify_backend.application.dto.Response.LoginResponse;

public interface LoginUsecase {
    LoginResponse execute(LoginRequest request);
}
