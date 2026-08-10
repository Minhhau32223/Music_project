package com.spotify.spotify_backend.application.usecase.usecaseimpl.userusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.ChangePasswordRequest;
import com.spotify.spotify_backend.application.usecase.PasswordHasher;
import com.spotify.spotify_backend.application.usecase.userusecase.ChangePasswordUseCase;
import com.spotify.spotify_backend.domain.exception.BadRequestException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public void execute(Long userId, ChangePasswordRequest request) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        boolean passwordMatches = passwordHasher.verifyPassword(request.currentPassword(), user.getPasswordHash());
        
        if (!passwordMatches) {
            throw new BadRequestException("Current password does not match");
        }
        
        String newPasswordHash = passwordHasher.hashPassword(request.newPassword());
        user.setPasswordHash(newPasswordHash);
        
        userRepository.save(user);
    }
}
