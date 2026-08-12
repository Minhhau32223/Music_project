package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.AddFavoriteSongUseCase;
import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.GetFavoriteSongsUseCase;
import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.RemoveFavoriteSongUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteSongController {

    private final AddFavoriteSongUseCase addFavoriteSongUseCase;
    private final RemoveFavoriteSongUseCase removeFavoriteSongUseCase;
    private final GetFavoriteSongsUseCase getFavoriteSongsUseCase;

    @PostMapping("/{songId}")
    public void addFavoriteSong(
            Authentication authentication,
            @PathVariable Long songId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        addFavoriteSongUseCase.execute(userId, songId);
    }

    @DeleteMapping("/{songId}")
    public void removeFavoriteSong(
            Authentication authentication,
            @PathVariable Long songId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        removeFavoriteSongUseCase.execute(userId, songId);
    }

    @GetMapping("/me")
    public List<SongResponse> getFavoriteSongs(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return getFavoriteSongsUseCase.execute(userId);
    }
}