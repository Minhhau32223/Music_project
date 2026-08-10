package com.spotify.spotify_backend.application.usecase.usecaseimpl.userusecaseimpl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.application.mapper.UserMapper;
import com.spotify.spotify_backend.application.usecase.userusecase.UploadAvatarUseCase;
import com.spotify.spotify_backend.domain.exception.FileUploadException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadAvatarUseCaseImpl implements UploadAvatarUseCase {

    private final UserRepository userRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public UserProfileResponse execute(Long userId, MultipartFile avatar) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        try {
            String avatarUrl = cloudinaryRepository.uploadFile(avatar, "avatars");
            user.setAvatarUrl(avatarUrl);
            User saved = userRepository.save(user);
            return UserMapper.toProfileResponse(saved);
        } catch (Exception e) {
            throw new FileUploadException("Upload avatar error: " + e.getMessage());
        }
    }
}
