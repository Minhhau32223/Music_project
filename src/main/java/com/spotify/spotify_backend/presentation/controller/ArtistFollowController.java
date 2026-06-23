package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.usecase.artistfollowusecase.*;

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

    @PostMapping("/{userId}/{artistId}")
    public void followArtist(
            @PathVariable Long userId,
            @PathVariable Long artistId
    ) {

        followArtistUseCase.execute(
                userId,
                artistId
        );
    }

    @DeleteMapping("/{userId}/{artistId}")
    public void unfollowArtist(
            @PathVariable Long userId,
            @PathVariable Long artistId
    ) {

        unfollowArtistUseCase.execute(
                userId,
                artistId
        );
    }

    @GetMapping("/{userId}")
    public List<ArtistResponse>
    getFollowedArtists(
            @PathVariable Long userId
    ) {

        return getFollowedArtistsUseCase
                .execute(userId);
    }
}