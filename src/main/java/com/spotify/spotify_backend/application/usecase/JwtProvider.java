package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.domain.model.Role;

public interface JwtProvider {

    String generateToken(Long userId, Role role);

    boolean validateToken(String token);

    Long extractUserId(String token);
    Role extractRole(String token);
}