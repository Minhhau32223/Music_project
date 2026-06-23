package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistsongusecaseimpl;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.AddSongToPlaylistUseCase;
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
            Long playlistId,
            AddSongToPlaylistRequest request
    ) {

        playlistRepository.findById(
                        playlistId
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Playlist not found"
                        )
                );

        songRepository.findById(
                        request.songId()
                )
                .orElseThrow(
                        () -> new RuntimeException(
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

            throw new RuntimeException(
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