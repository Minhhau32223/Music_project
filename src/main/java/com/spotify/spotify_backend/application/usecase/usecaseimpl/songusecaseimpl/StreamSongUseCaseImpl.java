package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.StreamingResponse;
import com.spotify.spotify_backend.application.usecase.songusecase.StreamSongUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamSongUseCaseImpl implements StreamSongUseCase {

    private final SongRepository songRepository;

    @Override
    public StreamingResponse execute(Long songId) {

        Song song = songRepository.findPublishedById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        return new StreamingResponse(
                song.getId(),
                song.getName(),
                song.getFilePath(),
                "audio/mpeg"
        );
    }
}
