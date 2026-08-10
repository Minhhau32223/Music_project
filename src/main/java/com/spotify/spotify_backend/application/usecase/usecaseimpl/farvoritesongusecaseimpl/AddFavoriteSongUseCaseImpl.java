package com.spotify.spotify_backend.application.usecase.usecaseimpl.farvoritesongusecaseimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.AddFavoriteSongUseCase;
import com.spotify.spotify_backend.domain.exception.ConflictException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.FavoriteSong;
import com.spotify.spotify_backend.domain.repository.FavoriteSongRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddFavoriteSongUseCaseImpl
        implements AddFavoriteSongUseCase {

    private final SongRepository songRepository;

    private final FavoriteSongRepository
            favoriteSongRepository;

    @Override
    public void execute(
            Long userId,
            Long songId
    ) {

        songRepository.findById(songId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Song not found"
                        )
                );

        boolean exists =
                favoriteSongRepository
                        .findByUserIdAndSongId(
                                userId,
                                songId
                        )
                        .isPresent();

        if (exists) {

            throw new ConflictException(
                    "Song already favorited"
            );
        }

        FavoriteSong favoriteSong =
                FavoriteSong.builder()
                        .userId(userId)
                        .songId(songId)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        favoriteSongRepository.save(
                favoriteSong
        );
    }
}