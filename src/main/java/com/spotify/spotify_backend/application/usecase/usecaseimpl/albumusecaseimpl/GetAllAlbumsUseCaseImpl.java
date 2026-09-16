package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.mapper.AlbumMapper;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAllAlbumsUseCase;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllAlbumsUseCaseImpl implements GetAllAlbumsUseCase {

    private final AlbumRepository albumRepository;

    @Override
    public List<AlbumResponse> execute() {

        return albumRepository.findAllPublished()
                .stream()
                .map(AlbumMapper::toResponse)
                .toList();
    }
}
