package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.RecentlyPlayedResponse;
import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.GetRecentlyPlayedUseCase;
import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.RecordPlaySongUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(
        "/api/recently-played"
)
@RequiredArgsConstructor
public class RecentlyPlayedController {

    private final RecordPlaySongUseCase
            recordPlaySongUseCase;

    private final GetRecentlyPlayedUseCase
            getRecentlyPlayedUseCase;

    @PostMapping(
            "/users/{userId}/songs/{songId}"
    )
    public void recordPlay(
            @PathVariable Long userId,
            @PathVariable Long songId
    ) {

        recordPlaySongUseCase.execute(
                userId,
                songId
        );
    }

    @GetMapping(
            "/users/{userId}"
    )
    public List<RecentlyPlayedResponse>
    getHistory(
            @PathVariable Long userId
    ) {

        return getRecentlyPlayedUseCase.execute(
                userId
        );
    }
}