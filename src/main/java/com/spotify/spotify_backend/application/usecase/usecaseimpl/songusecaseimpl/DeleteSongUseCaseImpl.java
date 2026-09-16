package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;


import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.songusecase.DeleteSongUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;
@Service
@lombok.extern.slf4j.Slf4j
@RequiredArgsConstructor
public class DeleteSongUseCaseImpl implements DeleteSongUseCase{
     private final SongRepository songRepository;

    @Override
    public void execute(Long id) {

        songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        songRepository.deleteById(id);
        log.info("Deleted song id={}", id);
    }
}
