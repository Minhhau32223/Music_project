package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.CreateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.mapper.AlbumMapper;
import com.spotify.spotify_backend.application.usecase.albumusecase.CreateAlbumUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateAlbumUseCaseImpl implements CreateAlbumUseCase {

    private final AlbumRepository albumRepository;

    @Override
    public AlbumResponse execute(CreateAlbumRequest request) {

        Album album = AlbumMapper.toDomain(request);

        Album savedAlbum = albumRepository.save(album);

        return AlbumMapper.toResponse(savedAlbum);
    }
}