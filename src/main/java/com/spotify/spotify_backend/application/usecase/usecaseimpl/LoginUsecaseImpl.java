package com.spotify.spotify_backend.application.usecase.usecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.LoginRequest;
import com.spotify.spotify_backend.application.dto.Response.LoginResponse;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.application.usecase.LoginUsecase;
import com.spotify.spotify_backend.application.usecase.PasswordHasher;
import com.spotify.spotify_backend.domain.exception.InvalidCredentialException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

@Service
public class LoginUsecaseImpl implements LoginUsecase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtProvider jwtProvider;
    public LoginUsecaseImpl(UserRepository userRepository, PasswordHasher passwordHasher, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtProvider = jwtProvider;
    }
    @Override
    public LoginResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email"));
        boolean passwordMatches = passwordHasher.verifyPassword(request.password(), user.getPasswordHash());
        if (!passwordMatches) {
            throw new InvalidCredentialException("Password does not match");
        }
        String accessToken =
        jwtProvider.generateToken(user.getId());
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),accessToken);
    }
}
