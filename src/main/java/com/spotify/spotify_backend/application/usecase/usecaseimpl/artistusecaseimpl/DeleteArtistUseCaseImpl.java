package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.artistusecase.DeleteArtistUseCase;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteArtistUseCaseImpl implements DeleteArtistUseCase {

    private final ArtistRepository artistRepository;

    @Override
    public void execute(Long id) {

        artistRepository.deleteById(id);
    }
}