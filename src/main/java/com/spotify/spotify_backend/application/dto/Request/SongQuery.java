package com.spotify.spotify_backend.application.dto.Request;

public record SongQuery(Long artistId, Long albumId, String sortBy,
                        boolean descending, int page, int size) {}
