package com.spotify.spotify_backend.presentation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.LoginRequest;
import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Response.LoginResponse;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
import com.spotify.spotify_backend.application.usecase.RegisterUseCase;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.application.usecase.RefreshAccessTokenUseCase;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.LoginUsecaseImpl;
import com.spotify.spotify_backend.infrastructure.security.RefreshTokenCookieService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUseCase registerUseCase;
    private final LoginUsecaseImpl loginUsecaseImpl;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final JwtProvider jwtProvider;
    private final RefreshTokenCookieService refreshTokenCookieService;

    @PostMapping("/register/")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(registerUseCase.register(request));
    }

    @PostMapping("/login/")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUsecaseImpl.execute(request);
        String refreshToken = jwtProvider.generateRefreshToken(response.id());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.create(refreshToken).toString())
                .body(response);
    }

    @PostMapping("/refresh/")
    public ResponseEntity<?> refresh(
            @CookieValue(value = RefreshTokenCookieService.COOKIE_NAME, required = false) String refreshToken) {
        return ResponseEntity.ok(refreshAccessTokenUseCase.execute(refreshToken));
    }

    @PostMapping("/logout/")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clear().toString())
                .build();
    }
}
