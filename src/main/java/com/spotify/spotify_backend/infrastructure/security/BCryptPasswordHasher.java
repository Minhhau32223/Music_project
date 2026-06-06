package com.spotify.spotify_backend.infrastructure.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.spotify.spotify_backend.application.usecase.PasswordHasher;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder
            encoder =
            new BCryptPasswordEncoder();
    @Override
    public String hashPassword(String password) {
        return encoder.encode(password);
    }
    @Override
    public Boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
