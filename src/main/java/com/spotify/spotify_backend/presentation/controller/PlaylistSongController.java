package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.AddSongToPlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.GetPlaylistSongsUseCase;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.RemoveSongFromPlaylistUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistSongController {

    private final AddSongToPlaylistUseCase
            addSongToPlaylistUseCase;

    private final RemoveSongFromPlaylistUseCase
            removeSongFromPlaylistUseCase;

    private final GetPlaylistSongsUseCase
            getPlaylistSongsUseCase;

    @PostMapping("/{playlistId}/songs")
    public void addSong(
            @PathVariable Long playlistId,
            @RequestBody AddSongToPlaylistRequest request
    ) {

        addSongToPlaylistUseCase.execute(
                playlistId,
                request
        );
    }

    @DeleteMapping(
            "/{playlistId}/songs/{songId}"
    )
    public void removeSong(
            @PathVariable Long playlistId,
            @PathVariable Long songId
    ) {

        removeSongFromPlaylistUseCase.execute(
                playlistId,
                songId
        );
    }

    @GetMapping("/{playlistId}/songs")
    public List<SongResponse> getSongs(
            @PathVariable Long playlistId
    ) {

        return getPlaylistSongsUseCase.execute(
                playlistId
        );
    }
}