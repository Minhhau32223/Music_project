package com.spotify.spotify_backend.application.usecase.usecaseimpl.recentlyplayedusecaseimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.RecordPlaySongUseCase;
import com.spotify.spotify_backend.domain.model.RecentlyPlayed;
import com.spotify.spotify_backend.domain.repository.RecentlyPlayedRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordPlaySongUseCaseImpl
        implements RecordPlaySongUseCase {

    private final UserRepository userRepository;

    private final SongRepository songRepository;

    private final RecentlyPlayedRepository
            recentlyPlayedRepository;

    @Override
    public void execute(
            Long userId,
            Long songId
    ) {

        userRepository.findById(
                userId
        ).orElseThrow(
                () -> new RuntimeException(
                        "User not found"
                )
        );

        songRepository.findById(
                songId
        ).orElseThrow(
                () -> new RuntimeException(
                        "Song not found"
                )
        );

        RecentlyPlayed history =
                RecentlyPlayed.builder()
                        .userId(userId)
                        .songId(songId)
                        .playedAt(
                                LocalDateTime.now()
                        )
                        .build();

        recentlyPlayedRepository.save(
                history
        );
    }
}