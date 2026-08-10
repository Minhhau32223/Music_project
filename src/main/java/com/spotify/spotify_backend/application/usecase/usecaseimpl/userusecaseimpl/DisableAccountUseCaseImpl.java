package com.spotify.spotify_backend.application.usecase.usecaseimpl.userusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.userusecase.DisableAccountUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisableAccountUseCaseImpl implements DisableAccountUseCase {

    private final UserRepository userRepository;

    @Override
    public void execute(Long userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        user.setActive(false);
        
        userRepository.save(user);
    }
}
