package com.spotify.spotify_backend.application.usecase.usecaseimpl.farvoritesongusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.RemoveFavoriteSongUseCase;
import com.spotify.spotify_backend.domain.repository.FavoriteSongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoveFavoriteSongUseCaseImpl
        implements RemoveFavoriteSongUseCase {

    private final FavoriteSongRepository
            favoriteSongRepository;

    @Override
    public void execute(
            Long userId,
            Long songId
    ) {
        
        favoriteSongRepository
                .deleteByUserIdAndSongId(
                        userId,
                        songId
                );
    }
}