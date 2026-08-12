package com.spotify.spotify_backend.application.usecase.usecaseimpl.recentlyplayedusecaseimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.RecordPlaybackUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.RecentlyPlayed;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.RecentlyPlayedRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import com.spotify.spotify_backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordPlaybackUseCaseImpl implements RecordPlaybackUseCase {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final RecentlyPlayedRepository recentlyPlayedRepository;

    @Override
    @Transactional
    public void execute(Long userId, Long songId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        // Tăng playCount
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);

        // Lưu lịch sử nghe nhạc
        RecentlyPlayed history = RecentlyPlayed.builder()
                .userId(userId)
                .songId(songId)
                .playedAt(LocalDateTime.now())
                .build();

        recentlyPlayedRepository.save(history);
    }
}
