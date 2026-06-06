package com.spotify.spotify_backend.application.usecase;

public interface JwtProvider {

    String generateToken(Long userId);

    boolean validateToken(String token);

    Long extractUserId(String token);
}