package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetAllArtistsUseCase;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllArtistsUseCaseImpl implements GetAllArtistsUseCase {

    private final ArtistRepository artistRepository;

    @Override
    public List<ArtistResponse> execute() {

        return artistRepository.findAll()
                .stream()
                .map(ArtistMapper::toResponse)
                .toList();
    }
}