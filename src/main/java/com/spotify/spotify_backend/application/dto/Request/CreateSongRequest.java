package com.spotify.spotify_backend.application.dto.Request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record CreateSongRequest(
    @NotBlank
    String name,
    @NotBlank
    String file,
    @NotBlank
    String image,
    @NotNull
    String lyrics,
    @NotNull
    String duration,
    @NotNull
    Long playCount,
    @NotNull
    Long albumId,
    @NotNull
    Long artistId
) {
    
}
