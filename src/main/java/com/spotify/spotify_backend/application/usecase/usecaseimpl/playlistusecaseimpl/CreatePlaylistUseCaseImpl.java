package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.mapper.PlaylistMapper;
import com.spotify.spotify_backend.application.usecase.playlistusecase.CreatePlaylistUseCase;
import com.spotify.spotify_backend.domain.model.Playlist;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CreatePlaylistUseCaseImpl implements CreatePlaylistUseCase{
    private final PlaylistRepository playlistRepository;

    @Override
    public PlaylistResponse execute(
            Long userId,
            CreatePlaylistRequest request
    ) {

        Playlist playlist =
                PlaylistMapper.toDomain(request);
        playlist.setUserId(userId);

        Playlist saved =
                playlistRepository.save(playlist);

        return PlaylistMapper.toResponse(saved);
    }
}
