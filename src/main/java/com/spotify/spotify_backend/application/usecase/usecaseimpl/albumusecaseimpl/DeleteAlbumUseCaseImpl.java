package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.albumusecase.DeleteAlbumUseCase;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteAlbumUseCaseImpl implements DeleteAlbumUseCase {

    private final AlbumRepository albumRepository;

    @Override
    public void execute(Long id) {

        albumRepository.deleteById(id);
    }
}