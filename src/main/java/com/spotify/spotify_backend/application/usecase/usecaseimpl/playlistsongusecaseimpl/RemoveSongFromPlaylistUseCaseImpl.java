package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistsongusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.usecase.playplistsongusecase.RemoveSongFromPlaylistUseCase;
import com.spotify.spotify_backend.domain.repository.PlaylistSongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoveSongFromPlaylistUseCaseImpl
        implements RemoveSongFromPlaylistUseCase {

    private final PlaylistSongRepository repository;

    @Override
    public void execute(
            Long playlistId,
            Long songId
    ) {

        repository.deleteByPlaylistIdAndSongId(
                playlistId,
                songId
        );
    }
}