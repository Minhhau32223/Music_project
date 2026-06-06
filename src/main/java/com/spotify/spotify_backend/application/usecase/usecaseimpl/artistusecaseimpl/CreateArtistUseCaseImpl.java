package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.usecase.artistusecase.CreateArtistUseCase;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateArtistUseCaseImpl implements CreateArtistUseCase {

    private final ArtistRepository artistRepository;

    @Override
    public ArtistResponse execute(CreateArtistRequest request) {

        Artist artist = ArtistMapper.toDomain(request);

        Artist savedArtist = artistRepository.save(artist);

        return ArtistMapper.toResponse(savedArtist);
    }
}