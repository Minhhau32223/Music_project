package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record AddSongToPlaylistRequest(
        @NotNull
        @Positive
        Long songId
) {
}
