package com.spotify.spotify_backend.application.dto.Response;

public record AlbumResponse(
    Long id,
    String name,
    String image,
    String description,
    String bgColor
) {
    
}
