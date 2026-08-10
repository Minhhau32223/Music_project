package com.spotify.spotify_backend.application.dto.Request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateAlbumRequest(
    String name,
    MultipartFile image,
    String description,
    String bgColor
) {}
