package com.spotify.spotify_backend.application.usecase.usecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.AccessTokenResponse;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.application.usecase.RefreshAccessTokenUseCase;
import com.spotify.spotify_backend.domain.exception.UnauthorizedException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshAccessTokenUseCaseImpl implements RefreshAccessTokenUseCase {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public AccessTokenResponse execute(String refreshToken) {
        if (refreshToken == null || !jwtProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        Long userId = jwtProvider.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .filter(candidate -> Boolean.TRUE.equals(candidate.getActive()))
                .orElseThrow(() -> new UnauthorizedException("Session is no longer available"));

        return new AccessTokenResponse(jwtProvider.generateToken(user.getId(), user.getRole()));
    }
}
