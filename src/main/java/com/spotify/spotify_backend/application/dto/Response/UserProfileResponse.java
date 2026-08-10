package com.spotify.spotify_backend.application.dto.Response;

import com.spotify.spotify_backend.domain.model.Role;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Boolean active,
        Role role,
        String avatarUrl
) {

}
