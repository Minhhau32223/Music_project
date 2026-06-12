package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.usecase.playlistusecase.CreatePlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.DeletePlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetAllPlaylistsUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetPlaylistUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final CreatePlaylistUseCase createPlaylistUseCase;
    private final GetPlaylistUseCase getPlaylistUseCase;
    private final GetAllPlaylistsUseCase getAllPlaylistsUseCase;
    private final DeletePlaylistUseCase deletePlaylistUseCase;

    @PostMapping("/create")
    public PlaylistResponse create(
            @RequestBody CreatePlaylistRequest request
    ) {
        return createPlaylistUseCase.execute(request);
    }

    @GetMapping("/")
    public List<PlaylistResponse> getAll() {
        return getAllPlaylistsUseCase.execute();
    }

    @GetMapping("/{id}")
    public PlaylistResponse getById(
            @PathVariable Long id
    ) {
        return getPlaylistUseCase.execute(id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        deletePlaylistUseCase.execute(id);
    }
}