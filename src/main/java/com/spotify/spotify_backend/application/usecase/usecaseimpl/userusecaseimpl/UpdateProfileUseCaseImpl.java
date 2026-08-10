package com.spotify.spotify_backend.application.usecase.usecaseimpl.userusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.UpdateProfileRequest;
import com.spotify.spotify_backend.application.dto.Response.UserProfileResponse;
import com.spotify.spotify_backend.application.mapper.UserMapper;
import com.spotify.spotify_backend.application.usecase.userusecase.UpdateProfileUseCase;
import com.spotify.spotify_backend.domain.exception.ConflictException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse execute(
            Long userId,
            UpdateProfileRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean usernameChanged = !user.getUsername().equals(request.username());
        boolean emailChanged = !user.getEmail().equals(request.email());

        if (usernameChanged && userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken");
        }

        if (emailChanged && userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already taken");
        }

        UserMapper.updateDomain(user, request);

        User saved = userRepository.save(user);

        return UserMapper.toProfileResponse(saved);
    }

}