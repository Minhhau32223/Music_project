package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.usecase.artistusecase.CreateArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.DeleteArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetAllArtistsUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.GetArtistUseCase;
import com.spotify.spotify_backend.application.usecase.artistusecase.UpdateArtistUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final CreateArtistUseCase createArtistUseCase;
    private final GetArtistUseCase getArtistUseCase;
    private final GetAllArtistsUseCase getAllArtistsUseCase;
    private final DeleteArtistUseCase deleteArtistUseCase;
    private final UpdateArtistUseCase updateArtistUseCase;

    @PostMapping("/create/")
    public ArtistResponse createArtist(
            @RequestBody CreateArtistRequest request) {

        return createArtistUseCase.execute(request);
    }

    @PutMapping("/{id}/")
    public ArtistResponse updateArtist(
            @PathVariable Long id,
            @ModelAttribute UpdateArtistRequest request) {

        return updateArtistUseCase.execute(id, request);
    }

    @GetMapping("/")
    public List<ArtistResponse> getAllArtists() {

        return getAllArtistsUseCase.execute();
    }

    @GetMapping("/{id}/")
    public ArtistResponse getArtist(
            @PathVariable Long id) {

        return getArtistUseCase.execute(id);
    }

    @DeleteMapping("/{id}/")
    public void deleteArtist(
            @PathVariable Long id) {

        deleteArtistUseCase.execute(id);
    }
}