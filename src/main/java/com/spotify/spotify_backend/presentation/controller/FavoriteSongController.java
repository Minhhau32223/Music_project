package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

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

    private final AddFavoriteSongUseCase
            addFavoriteSongUseCase;

    private final RemoveFavoriteSongUseCase
            removeFavoriteSongUseCase;

    private final GetFavoriteSongsUseCase
            getFavoriteSongsUseCase;

    @PostMapping("/{userId}/{songId}")
    public void addFavoriteSong(
            @PathVariable Long userId,
            @PathVariable Long songId
    ) {

        addFavoriteSongUseCase.execute(
                userId,
                songId
        );
    }

    @DeleteMapping("/{userId}/{songId}")
    public void removeFavoriteSong(
            @PathVariable Long userId,
            @PathVariable Long songId
    ) {

        removeFavoriteSongUseCase.execute(
                userId,
                songId
        );
    }

    @GetMapping("/{userId}")
    public List<SongResponse> getFavoriteSongs(
            @PathVariable Long userId
    ) {

        return getFavoriteSongsUseCase.execute(
                userId
        );
    }
}