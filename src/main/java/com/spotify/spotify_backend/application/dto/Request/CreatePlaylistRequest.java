package com.spotify.spotify_backend.application.dto.Request;
import jakarta.validation.constraints.NotBlank;
public record CreatePlaylistRequest(
        @NotBlank
        String name,
        String image
) {}
