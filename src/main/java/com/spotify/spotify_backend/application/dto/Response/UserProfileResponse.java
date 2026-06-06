package com.spotify.spotify_backend.application.dto.Response;

public record UserProfileResponse( Long id,
        String username,
        String email,
        Boolean active,
        Boolean admin
        ) {
    
}
