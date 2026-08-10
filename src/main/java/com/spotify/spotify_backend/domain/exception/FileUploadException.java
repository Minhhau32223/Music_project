package com.spotify.spotify_backend.domain.exception;

public class FileUploadException
        extends RuntimeException {

    public FileUploadException(
            String message
    ) {
        super(message);
    }
}