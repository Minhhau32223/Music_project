package com.spotify.spotify_backend.presentation.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.application.usecase.GetCurrentUserUseCase;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
        private final GetCurrentUserUseCase getCurrentUserUseCase;
        private final JwtProvider jwtProvider;
        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser(Authentication authentication) {
            try {
                Long userId = Long.valueOf(authentication.getName());
                UserProfileResponse response = getCurrentUserUseCase.execute(userId);
                return ResponseEntity.ok(response);
            } catch (RuntimeException e) {
                return ResponseEntity.status(401).body(e.getMessage());
            }
        }
    
}
