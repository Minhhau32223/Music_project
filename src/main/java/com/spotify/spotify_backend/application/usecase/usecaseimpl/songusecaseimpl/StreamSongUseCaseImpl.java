package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import com.spotify.spotify_backend.application.usecase.songusecase.StreamSongUseCase;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StreamSongUseCaseImpl implements StreamSongUseCase {

    private final SongRepository songRepository;

    @Override
    public Song execute(Long songId) {

        return songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
    }
}