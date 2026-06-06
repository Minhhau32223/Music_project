package com.spotify.spotify_backend.application.dto.Response;

public record RegisterResponse(  
    Long id,
    String username,
    String email
) {

}
