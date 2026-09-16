package com.spotify.spotify_backend.application.dto.Request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateArtistSongRequest(
        @NotBlank String name,
        @NotNull MultipartFile file,
        @NotNull MultipartFile image,
        @NotBlank String lyrics,
        @NotBlank String duration,
        @NotNull Long albumId) {}
