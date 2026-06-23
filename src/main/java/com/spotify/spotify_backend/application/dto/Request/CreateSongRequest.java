package com.spotify.spotify_backend.application.dto.Request;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record CreateSongRequest(
    @NotBlank
    String name,
    MultipartFile file,
    MultipartFile image,
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
