package com.spotify.spotify_backend.application.usecase.userusecase;

import org.springframework.web.multipart.MultipartFile;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;

public interface UploadAvatarUseCase {
    UserProfileResponse execute(Long userId, MultipartFile avatar);
}
