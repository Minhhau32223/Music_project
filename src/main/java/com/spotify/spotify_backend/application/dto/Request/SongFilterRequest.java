package com.spotify.spotify_backend.application.dto.Request;

public record SongFilterRequest(
        Long artistId,
        Long albumId,
        String sortBy,
        String sortDirection,
        Integer page,
        Integer size
) {}
