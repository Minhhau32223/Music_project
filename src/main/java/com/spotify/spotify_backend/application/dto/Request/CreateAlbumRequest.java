package com.spotify.spotify_backend.application.dto.Request;

public record CreateAlbumRequest(
    String name,
    String image,
    String description,
    String bgColor
) {
    
}
