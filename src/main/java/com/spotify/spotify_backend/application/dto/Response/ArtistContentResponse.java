package com.spotify.spotify_backend.application.dto.Response;

import java.time.LocalDateTime;

public record ArtistContentResponse(Long id, String name, Long artistId, Long albumId,
        String status, String rejectionReason, LocalDateTime createdAt,
        LocalDateTime updatedAt, LocalDateTime publishedAt, String file, String image,
        String lyrics, String duration) {}
