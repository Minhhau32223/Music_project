package com.spotify.spotify_backend.application.dto.Response;

public record StreamingResponse(
        Long songId,
        String songName,
        String streamUrl,
        String contentType
) {}
