package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.playlistusecase.DeletePlaylistUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class DeletePlaylistUseCaseImpl
        implements DeletePlaylistUseCase {

    private final PlaylistRepository repository;

    @Override
    public void execute(Long id) {

        repository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Playlist not found"
                        )
                );

        repository.deleteById(id);
    }
}