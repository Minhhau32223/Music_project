package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.usecase.artistfollowusecase.FollowArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistfollowusecase.GetFollowedArtistsUseCase;
import com.spotify.spotify_backend.application.usecase.artistfollowusecase.UnfollowArtistUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artist-follows")
@RequiredArgsConstructor
public class ArtistFollowController {

    private final FollowArtistUseCase followArtistUseCase;
    private final UnfollowArtistUseCase unfollowArtistUseCase;
    private final GetFollowedArtistsUseCase getFollowedArtistsUseCase;

    @PostMapping({"/{artistId}", "/{artistId}/"})
    public void followArtist(
            Authentication authentication,
            @PathVariable Long artistId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        followArtistUseCase.execute(userId, artistId);
    }

    @DeleteMapping({"/{artistId}", "/{artistId}/"})
    public void unfollowArtist(
            Authentication authentication,
            @PathVariable Long artistId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        unfollowArtistUseCase.execute(userId, artistId);
    }

    @GetMapping({"/me", "/me/"})
    public List<ArtistResponse> getFollowedArtists(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return getFollowedArtistsUseCase.execute(userId);
    }
}
