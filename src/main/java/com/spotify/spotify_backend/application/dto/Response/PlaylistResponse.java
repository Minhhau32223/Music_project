package com.spotify.spotify_backend.application.dto.Response;

public record PlaylistResponse(
        Long id,
        String name,
        String image,
        Long userId
) {}