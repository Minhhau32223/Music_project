package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetNewReleasesUseCase;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetPersonalizedRecommendationsUseCase;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetTopSongsUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final GetTopSongsUseCase getTopSongsUseCase;
    private final GetNewReleasesUseCase getNewReleasesUseCase;
    private final GetPersonalizedRecommendationsUseCase getPersonalizedRecommendationsUseCase;

    @GetMapping("/top-songs")
    public ResponseEntity<List<SongResponse>> getTopSongs(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(getTopSongsUseCase.execute(limit));
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<SongResponse>> getNewReleases(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(getNewReleasesUseCase.execute(limit));
    }

    @GetMapping("/personalized")
    public ResponseEntity<List<SongResponse>> getPersonalized(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(getPersonalizedRecommendationsUseCase.execute(userId, limit));
    }
}
