package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record UpdateArtistProfileRequest(@NotBlank String name, @NotBlank String bio, @NotBlank String profileImage) {}
