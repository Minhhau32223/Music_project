package com.spotify.spotify_backend.application.dto.Request;

import org.springframework.web.multipart.MultipartFile;

public record UploadSongRequest(
    MultipartFile file,
    MultipartFile image
) {
}
