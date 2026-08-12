package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.NotNull;

public record PlaybackEventRequest(
        @NotNull(message = "Song ID is required")
        Long songId
) {}
