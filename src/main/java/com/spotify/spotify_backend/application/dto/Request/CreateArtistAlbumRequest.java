package com.spotify.spotify_backend.application.dto.Request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;

public record CreateArtistAlbumRequest(
        @NotBlank String name,
        MultipartFile image,
        @NotBlank String description,
        @NotBlank String bgColor) {}
