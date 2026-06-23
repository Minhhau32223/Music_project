package com.spotify.spotify_backend.presentation.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.CreateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.usecase.songusecase.CreateSongUseCase;
import com.spotify.spotify_backend.application.usecase.songusecase.DeleteSongUseCase;
import com.spotify.spotify_backend.application.usecase.songusecase.GetAllSongsUseCase;
import com.spotify.spotify_backend.application.usecase.songusecase.GetSongUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final CreateSongUseCase createSongUseCase;
    private final GetSongUseCase getSongUseCase;
    private final GetAllSongsUseCase getAllSongsUseCase;
    private final DeleteSongUseCase deleteSongUseCase;

    /**
     * GET /api/songs/
     */
    @GetMapping("/")
    public ResponseEntity<List<SongResponse>> getAllSongs() {

        return ResponseEntity.ok(
                getAllSongsUseCase.execute()
        );
    }

    /**
     * GET /api/songs/{id}/
     */
    @GetMapping("/{id}/")
    public ResponseEntity<SongResponse> getSong(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                getSongUseCase.execute(id)
        );
    }

    /**
     * POST /api/songs/create/
     */
   @PostMapping(value = "/create/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<SongResponse> createSong(
        @Valid @ModelAttribute CreateSongRequest request
) {
    return ResponseEntity.ok(
            createSongUseCase.execute(request)
    );
}
    /**
     * DELETE /api/songs/{id}/
     */
    @DeleteMapping("/{id}/")
    public ResponseEntity<Void> deleteSong(
            @PathVariable Long id
    ) {

        deleteSongUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}