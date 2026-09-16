package com.spotify.spotify_backend.application.dto.Request;
import jakarta.validation.constraints.NotBlank;
public record CreateArtistApplicationRequest(@NotBlank String artistName, @NotBlank String bio, @NotBlank String profileImage) {}
