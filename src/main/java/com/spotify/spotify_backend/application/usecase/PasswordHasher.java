package com.spotify.spotify_backend.application.usecase;

public interface PasswordHasher {
    String hashPassword(String password);
    Boolean verifyPassword(String rawPassword, String hashedPassword);
}
