package com.spotify.spotify_backend.application.dto.Request;
import io.swagger.v3.oas.annotations.media.Schema;
public record SongFilterRequest(
        Long artistId,
        Long albumId,
        @Schema(allowableValues = {"playCount", "id", "createdAt", "name"}, defaultValue = "id") String sortBy,
        @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "ASC") String sortDirection,
        @Schema(minimum = "0", defaultValue = "0") Integer page,
        @Schema(minimum = "1", maximum = "100", defaultValue = "10") Integer size
) {}
