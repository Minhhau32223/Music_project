    package com.spotify.spotify_backend.domain.exception;

public class BadRequestException
        extends RuntimeException {

    public BadRequestException(
            String message
    ) {
        super(message);
    }
}