package com.spotify.spotify_backend.application.dto.Response;

import lombok.Builder;

@Builder
public record PlaylistSongResponse(
        Long id,
        Long playlistId,
        Long songId
) {
}