package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.usecase.playlistusecase.CreatePlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.DeletePlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetAllPlaylistsUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.GetPlaylistUseCase;
import com.spotify.spotify_backend.application.usecase.playlistusecase.UpdatePlaylistUseCase;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final CreatePlaylistUseCase createPlaylistUseCase;
    private final GetPlaylistUseCase getPlaylistUseCase;
    private final GetAllPlaylistsUseCase getAllPlaylistsUseCase;
    private final DeletePlaylistUseCase deletePlaylistUseCase;
    private final UpdatePlaylistUseCase updatePlaylistUseCase;

    @PostMapping("/create")
    public PlaylistResponse create(
            Authentication authentication,
            @Valid @RequestBody CreatePlaylistRequest request
    ) {
        return createPlaylistUseCase.execute((Long) authentication.getPrincipal(), request);
    }

    @PutMapping("/{id}")
    public PlaylistResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @ModelAttribute UpdatePlaylistRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return updatePlaylistUseCase.execute(userId, id, request);
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
            Authentication authentication,
            @PathVariable Long id
    ) {
        deletePlaylistUseCase.execute((Long) authentication.getPrincipal(), id);
    }
}
