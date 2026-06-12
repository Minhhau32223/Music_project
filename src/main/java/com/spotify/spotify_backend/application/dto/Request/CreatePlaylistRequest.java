package com.spotify.spotify_backend.application.dto.Request;

public record CreatePlaylistRequest(
        String name,
        String image,
        Long userId
) {}