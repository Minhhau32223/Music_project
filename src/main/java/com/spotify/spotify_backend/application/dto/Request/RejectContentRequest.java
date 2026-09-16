package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record RejectContentRequest(@NotBlank String rejectionReason) {}
