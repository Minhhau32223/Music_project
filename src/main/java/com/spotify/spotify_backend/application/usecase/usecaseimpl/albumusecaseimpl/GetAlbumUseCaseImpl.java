package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.mapper.AlbumMapper;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAlbumUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAlbumUseCaseImpl implements GetAlbumUseCase {

    private final AlbumRepository albumRepository;

    @Override
    public AlbumResponse execute(Long id) {

        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        return AlbumMapper.toResponse(album);
    }
}