package com.spotify.spotify_backend.application.dto.Response;

public record ArtistResponse(
    Long id,
    String name,
    String bio,
    String profileImage
) {
    
}
