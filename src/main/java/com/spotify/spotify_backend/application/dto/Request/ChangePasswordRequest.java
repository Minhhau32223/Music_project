package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank 
    String currentPassword,
    
    @NotBlank 
    @Size(min = 8, message = "Password must be at least 8 characters") 
    String newPassword
) {}
