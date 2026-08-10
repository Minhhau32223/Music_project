package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

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

    private final FollowArtistUseCase
            followArtistUseCase;

    private final UnfollowArtistUseCase
            unfollowArtistUseCase;

    private final GetFollowedArtistsUseCase
            getFollowedArtistsUseCase;

    @PostMapping("/{userId}/{artistId}/")
    public void followArtist(
            @PathVariable Long userId,
            @PathVariable Long artistId
    ) {

        followArtistUseCase.execute(
                userId,
                artistId
        );
    }

    @DeleteMapping("/{userId}/{artistId}/")
    public void unfollowArtist(
            @PathVariable Long userId,
            @PathVariable Long artistId
    ) {

        unfollowArtistUseCase.execute(
                userId,
                artistId
        );
    }

    @GetMapping("/{userId}/")
    public List<ArtistResponse>
    getFollowedArtists(
            @PathVariable Long userId
    ) {

        return getFollowedArtistsUseCase
                .execute(userId);
    }
}