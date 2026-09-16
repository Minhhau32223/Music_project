package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.domain.exception.BadRequestException;

public final class QueryLimits {
    public static final int MAX_SIZE = 100;

    private QueryLimits() {}

    public static void validateLimit(int limit) {
        if (limit < 1 || limit > MAX_SIZE) {
            throw new BadRequestException("limit must be between 1 and " + MAX_SIZE);
        }
    }
}
