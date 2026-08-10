package com.spotify.spotify_backend.application.dto.Request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSongRequest(

        @NotBlank
        String name,

        MultipartFile file,

        MultipartFile image,

        @NotBlank
        String lyrics,

        @NotBlank
        String duration,

        @NotNull
        Long albumId,

        @NotNull
        Long artistId

) {
}