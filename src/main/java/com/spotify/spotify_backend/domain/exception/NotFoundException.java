package com.spotify.spotify_backend.domain.exception;

public class NotFoundException
        extends RuntimeException {

    public NotFoundException(
            String message
    ) {
        super(message);
    }
}