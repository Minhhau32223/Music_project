package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.PlaybackEventRequest;
import com.spotify.spotify_backend.application.dto.Response.RecentlyPlayedResponse;
import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.GetRecentlyPlayedUseCase;
import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.RecordPlaybackUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recently-played")
@RequiredArgsConstructor
public class RecentlyPlayedController {

    private final RecordPlaybackUseCase recordPlaybackUseCase;
    private final GetRecentlyPlayedUseCase getRecentlyPlayedUseCase;

    @PostMapping("/play")
    public void recordPlay(
            Authentication authentication,
            @Valid @RequestBody PlaybackEventRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        recordPlaybackUseCase.execute(userId, request.songId());
    }

    @GetMapping("/me")
    public List<RecentlyPlayedResponse> getHistory(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return getRecentlyPlayedUseCase.execute(userId);
    }
}