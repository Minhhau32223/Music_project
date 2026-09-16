package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.domain.model.Role;

public interface JwtProvider {

    String generateToken(Long userId, Role role);

    String generateRefreshToken(Long userId);

    boolean validateToken(String token);

    boolean validateRefreshToken(String token);

    Long extractUserId(String token);
    Role extractRole(String token);
}
