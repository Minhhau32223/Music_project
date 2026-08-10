package com.spotify.spotify_backend.application.usecase.usecaseimpl;
import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.application.usecase.GetCurrentUserUseCase;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;
import com.spotify.spotify_backend.domain.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {
    private final UserRepository userRepository;
    @Override
    public UserProfileResponse execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getActive(), user.getRole(), user.getAvatarUrl());
    }
    
}
