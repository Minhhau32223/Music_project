package com.spotify.spotify_backend.application.usecase.usecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
import com.spotify.spotify_backend.application.mapper.UserMapper;
import com.spotify.spotify_backend.application.usecase.PasswordHasher;
import com.spotify.spotify_backend.application.usecase.RegisterUseCase;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUseCaseImpl implements RegisterUseCase {

    private final UserRepository userRepository;

    private final PasswordHasher passwordHasher;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Check if the username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Hash the password
        String hashedPassword = passwordHasher.hashPassword(request.password());

        // Create a new User entity
        User user = UserMapper.toUser(request);
        user.setPasswordHash(hashedPassword);

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Map the saved user to a RegisterResponse DTO and return it
        return UserMapper.toRegisterResponse(savedUser);
    }
}
