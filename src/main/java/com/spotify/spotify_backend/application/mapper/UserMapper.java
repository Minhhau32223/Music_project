package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateProfileRequest;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.domain.model.User;

public class UserMapper {

    private UserMapper() {
        // Private constructor to prevent instantiation
    }

    public static User toUser(RegisterRequest request) {

        User user = new User();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(request.password());
        user.setActive(true);
        user.setRole(Role.USER);

        return user;
    }

    public static void updateDomain(
            User user,
            UpdateProfileRequest request
    ) {
        user.setUsername(request.username());
        user.setEmail(request.email());
    }

    public static UserProfileResponse toProfileResponse(User user) {

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getActive(),
                user.getRole(),
                user.getAvatarUrl()
        );
    }

    public static RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

}