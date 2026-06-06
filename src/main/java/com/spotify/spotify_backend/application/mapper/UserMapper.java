package com.spotify.spotify_backend.application.mapper;
import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
import com.spotify.spotify_backend.domain.model.User;

public class UserMapper {

    private UserMapper() {
        // Private constructor to prevent instantiation
    }
    public static User toUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(request.password()); // In a real application, hash the password
        user.setActive(true);
        user.setAdmin(false);
        return user;
    }

    public static RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}