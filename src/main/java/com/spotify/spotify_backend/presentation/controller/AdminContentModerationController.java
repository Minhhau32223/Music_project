package com.spotify.spotify_backend.presentation.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.spotify.spotify_backend.application.dto.Request.RejectContentRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistContentResponse;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.ArtistContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminContentModerationController {
    private final ArtistContentService service;
    @GetMapping("/songs/pending") public List<ArtistContentResponse> pendingSongs(){ return service.pendingSongs(); }
    @PatchMapping("/songs/{id}/approve") public ArtistContentResponse approveSong(@PathVariable Long id){ return service.approveSong(id); }
    @PatchMapping("/songs/{id}/reject") public ArtistContentResponse rejectSong(@PathVariable Long id,@Valid @RequestBody RejectContentRequest r){ return service.rejectSong(id,r); }
    @PatchMapping("/songs/{id}/disable") public ArtistContentResponse disableSong(@PathVariable Long id){ return service.disableSong(id); }
    @GetMapping("/albums/pending") public List<ArtistContentResponse> pendingAlbums(){ return service.pendingAlbums(); }
    @PatchMapping("/albums/{id}/approve") public ArtistContentResponse approveAlbum(@PathVariable Long id){ return service.approveAlbum(id); }
    @PatchMapping("/albums/{id}/reject") public ArtistContentResponse rejectAlbum(@PathVariable Long id,@Valid @RequestBody RejectContentRequest r){ return service.rejectAlbum(id,r); }
    @PatchMapping("/albums/{id}/disable") public ArtistContentResponse disableAlbum(@PathVariable Long id){ return service.disableAlbum(id); }
}
