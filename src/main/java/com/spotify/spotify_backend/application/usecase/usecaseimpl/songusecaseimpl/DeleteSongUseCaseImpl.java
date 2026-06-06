package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;


import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.songusecase.DeleteSongUseCase;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class DeleteSongUseCaseImpl implements DeleteSongUseCase{
     private final SongRepository songRepository;

    @Override
    public void execute(Long id) {

        songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        songRepository.deleteById(id);
    }
}
