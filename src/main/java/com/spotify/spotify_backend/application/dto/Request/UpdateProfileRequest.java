package com.spotify.spotify_backend.application.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record  UpdateProfileRequest(
      @NotBlank
        String username,

        @Email
        @NotBlank
        String email

) {

}
