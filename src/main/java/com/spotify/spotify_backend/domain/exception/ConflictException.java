package com.spotify.spotify_backend.domain.exception;

public class ConflictException
        extends RuntimeException {

    public ConflictException(
            String message
    ) {
        super(message);
    }
}