package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistsongusecaseimpl;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.AddSongToPlaylistUseCase;
import com.spotify.spotify_backend.domain.exception.ConflictException;
import com.spotify.spotify_backend.domain.exception.ForbiddenException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.PlaylistSong;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;
import com.spotify.spotify_backend.domain.repository.PlaylistSongRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddSongToPlaylistUseCaseImpl
        implements AddSongToPlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    private final SongRepository songRepository;

    private final PlaylistSongRepository playlistSongRepository;

    @Override
    public void execute(
            Long userId,
            Long playlistId,
            AddSongToPlaylistRequest request
    ) {

        var playlist = playlistRepository.findById(
                        playlistId
                )
                .orElseThrow(
                        () -> new NotFoundException(
                                "Playlist not found"
                        )
                );

        if (!userId.equals(playlist.getUserId())) {
            throw new ForbiddenException("You do not own this playlist");
        }

        songRepository.findById(
                        request.songId()
                )
                .orElseThrow(
                        () -> new NotFoundException(
                                "Song not found"
                        )
                );

        boolean exists =
                playlistSongRepository
                        .findByPlaylistIdAndSongId(
                                playlistId,
                                request.songId()
                        )
                        .isPresent();

        if (exists) {

            throw new ConflictException(
                    "Song already exists in playlist"
            );
        }

        PlaylistSong playlistSong =
                PlaylistSong.builder()
                        .playlistId(playlistId)
                        .songId(request.songId())
                        .addedAt(
                                LocalDateTime.now()
                        )
                        .build();

        playlistSongRepository.save(
                playlistSong
        );
    }
}
