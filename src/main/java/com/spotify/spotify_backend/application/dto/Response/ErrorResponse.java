package com.spotify.spotify_backend.application.dto.Response;

import java.time.LocalDateTime;

public record ErrorResponse(
      LocalDateTime timestamp,

        int status,

        String error,

        String message,

        String path
) {

}
