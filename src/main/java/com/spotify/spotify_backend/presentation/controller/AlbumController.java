package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.spotify.spotify_backend.application.dto.Request.CreateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.usecase.albumusecase.CreateAlbumUseCase;
import com.spotify.spotify_backend.application.usecase.albumusecase.DeleteAlbumUseCase;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAlbumSongsUseCase;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAlbumUseCase;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAllAlbumsUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final CreateAlbumUseCase createAlbumUseCase;
    private final GetAlbumUseCase getAlbumUseCase;
    private final GetAllAlbumsUseCase getAllAlbumsUseCase;
    private final DeleteAlbumUseCase deleteAlbumUseCase;
    private final GetAlbumSongsUseCase getAlbumSongsUseCase;

    @PostMapping("/create")
    public AlbumResponse createAlbum(
            @RequestBody CreateAlbumRequest request
    ) {
        return createAlbumUseCase.execute(request);
    }

    @GetMapping
    public List<AlbumResponse> getAllAlbums() {
        return getAllAlbumsUseCase.execute();
    }

    @GetMapping("/{id}")
    public AlbumResponse getAlbum(
            @PathVariable Long id
    ) {
        return getAlbumUseCase.execute(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAlbum(
            @PathVariable Long id
    ) {
        deleteAlbumUseCase.execute(id);
    }

    @GetMapping("/{id}/songs")
    public List<SongResponse> getAlbumSongs(
            @PathVariable Long id
    ) {
        return getAlbumSongsUseCase.execute(id);
    }
}