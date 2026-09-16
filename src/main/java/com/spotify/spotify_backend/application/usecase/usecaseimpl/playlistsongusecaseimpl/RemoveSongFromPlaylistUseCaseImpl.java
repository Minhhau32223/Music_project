package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistsongusecaseimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.application.usecase.playplistsongusecase.RemoveSongFromPlaylistUseCase;
import com.spotify.spotify_backend.domain.exception.ForbiddenException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.repository.PlaylistSongRepository;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoveSongFromPlaylistUseCaseImpl
        implements RemoveSongFromPlaylistUseCase {

    private final PlaylistSongRepository repository;
    private final PlaylistRepository playlistRepository;

    @Override
    @Transactional
    public void execute(
            Long userId,
            Long playlistId,
            Long songId
    ) {

        var playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new NotFoundException("Playlist not found"));
        if (!userId.equals(playlist.getUserId())) {
            throw new ForbiddenException("You do not own this playlist");
        }
        repository.deleteByPlaylistIdAndSongId(
                playlistId,
                songId
        );
    }
}
