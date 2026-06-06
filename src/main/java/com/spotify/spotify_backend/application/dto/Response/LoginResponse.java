package com.spotify.spotify_backend.application.dto.Response;

public record LoginResponse(
    Long id,
    String username,
    String email,
    String accessToken

) {
    
}
