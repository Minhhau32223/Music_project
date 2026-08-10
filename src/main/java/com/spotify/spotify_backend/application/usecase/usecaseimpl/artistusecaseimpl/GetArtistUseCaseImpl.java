package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetArtistUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetArtistUseCaseImpl implements GetArtistUseCase {

    private final ArtistRepository artistRepository;

    @Override
    public ArtistResponse execute(Long id) {

        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        return ArtistMapper.toResponse(artist);
    }
}