package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.mapper.PlaylistMapper;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetAllPlaylistsUseCase;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllPlaylistsUseCaseImpl implements GetAllPlaylistsUseCase {
    private final PlaylistRepository repository;

    @Override
    public List<PlaylistResponse> execute() {

        return repository.findAll()
                .stream()
                .map(
                        PlaylistMapper::toResponse
                )
                .toList();
    }
}   