package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.mapper.PlaylistMapper;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetPlaylistUseCase;
import com.spotify.spotify_backend.domain.model.Playlist;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPlaylistUseCaseImpl implements GetPlaylistUseCase {
    private final PlaylistRepository repository;

    @Override
    public PlaylistResponse execute(Long id) {

        Playlist playlist =
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Playlist not found"
                                )
                        );

        return PlaylistMapper.toResponse(
                playlist
        );
    }
}
