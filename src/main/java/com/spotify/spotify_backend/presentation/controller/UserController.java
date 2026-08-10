package com.spotify.spotify_backend.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spotify.spotify_backend.application.dto.Request.ChangePasswordRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateProfileRequest;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.application.usecase.GetCurrentUserUseCase;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.application.usecase.userusecase.ChangePasswordUseCase;
import com.spotify.spotify_backend.application.usecase.userusecase.DisableAccountUseCase;
import com.spotify.spotify_backend.application.usecase.userusecase.UpdateProfileUseCase;
import com.spotify.spotify_backend.application.usecase.userusecase.UploadAvatarUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {
        private final GetCurrentUserUseCase getCurrentUserUseCase;
        private final UpdateProfileUseCase updateProfileUseCase;
        private final UploadAvatarUseCase uploadAvatarUseCase;
        private final ChangePasswordUseCase changePasswordUseCase;
        private final DisableAccountUseCase disableAccountUseCase;
        private final JwtProvider jwtProvider;
        
        @GetMapping("/me/")
        public ResponseEntity<?> getCurrentUser(Authentication authentication) {
            try {
                Long userId = Long.valueOf(authentication.getName());
                UserProfileResponse response = getCurrentUserUseCase.execute(userId);
                return ResponseEntity.ok(response);
            } catch (RuntimeException e) {
                return ResponseEntity.status(401).body(e.getMessage());
            }
        }
        
        @PutMapping("/profile/")
        public ResponseEntity<UserProfileResponse> updateProfile(

                Authentication authentication,

                @Valid
                @RequestBody
                UpdateProfileRequest request
        ) {

            Long userId =
                    (Long) authentication.getPrincipal();

            return ResponseEntity.ok(

                    updateProfileUseCase.execute(
                            userId,
                            request
                    )
            );
        }
        
        @PutMapping("/avatar/")
        public ResponseEntity<UserProfileResponse> uploadAvatar(
                Authentication authentication,
                @RequestParam("avatar") MultipartFile avatar
        ) {
            Long userId = (Long) authentication.getPrincipal();
            return ResponseEntity.ok(uploadAvatarUseCase.execute(userId, avatar));
        }

        @PutMapping("/change-password/")
        public ResponseEntity<Void> changePassword(
                Authentication authentication,
                @Valid @RequestBody ChangePasswordRequest request
        ) {
            Long userId = (Long) authentication.getPrincipal();
            changePasswordUseCase.execute(userId, request);
            return ResponseEntity.ok().build();
        }

        @DeleteMapping("/me/")
        public ResponseEntity<Void> disableAccount(
                Authentication authentication
        ) {
            Long userId = (Long) authentication.getPrincipal();
            disableAccountUseCase.execute(userId);
            return ResponseEntity.noContent().build();
        }
    
}
