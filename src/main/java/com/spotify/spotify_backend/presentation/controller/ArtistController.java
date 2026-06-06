package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.usecase.artistusecase.CreateArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.DeleteArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetAllArtistsUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetArtistUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final CreateArtistUseCase createArtistUseCase;
    private final GetArtistUseCase getArtistUseCase;
    private final GetAllArtistsUseCase getAllArtistsUseCase;
    private final DeleteArtistUseCase deleteArtistUseCase;

    @PostMapping("/create")
    public ArtistResponse createArtist(
            @RequestBody CreateArtistRequest request) {

        return createArtistUseCase.execute(request);
    }

    @GetMapping
    public List<ArtistResponse> getAllArtists() {

        return getAllArtistsUseCase.execute();
    }

    @GetMapping("/{id}")
    public ArtistResponse getArtist(
            @PathVariable Long id) {

        return getArtistUseCase.execute(id);
    }

    @DeleteMapping("/{id}")
    public void deleteArtist(
            @PathVariable Long id) {

        deleteArtistUseCase.execute(id);
    }
}