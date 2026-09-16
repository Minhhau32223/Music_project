package com.spotify.spotify_backend.presentation.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import com.spotify.spotify_backend.application.dto.Request.*;
import com.spotify.spotify_backend.application.dto.Response.*;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.ArtistContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
public class ArtistContentController {
    private final ArtistContentService service;
    private Long userId(Authentication a) { return (Long) a.getPrincipal(); }

    @GetMapping("/me") public ArtistResponse me(Authentication a) { return service.profile(userId(a)); }
    @PutMapping("/me") public ArtistResponse updateMe(Authentication a, @Valid @RequestBody UpdateArtistProfileRequest r) { return service.updateProfile(userId(a), r.name(), r.bio(), r.profileImage()); }

    @PostMapping(value="/albums", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ArtistContentResponse createAlbum(Authentication a, @Valid @ModelAttribute CreateArtistAlbumRequest r) { return service.createAlbum(userId(a),r); }
    @GetMapping("/albums") public List<ArtistContentResponse> albums(Authentication a) { return service.albums(userId(a)); }
    @PutMapping(value="/albums/{id}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ArtistContentResponse updateAlbum(Authentication a,@PathVariable Long id,@Valid @ModelAttribute UpdateArtistAlbumRequest r) { return service.updateAlbum(userId(a),id,r); }
    @DeleteMapping("/albums/{id}") public ResponseEntity<Void> deleteAlbum(Authentication a,@PathVariable Long id) { service.deleteAlbum(userId(a),id); return ResponseEntity.noContent().build(); }
    @PatchMapping("/albums/{id}/submit") public ArtistContentResponse submitAlbum(Authentication a,@PathVariable Long id) { return service.submitAlbum(userId(a),id); }

    @PostMapping(value="/songs", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ArtistContentResponse createSong(Authentication a,@Valid @ModelAttribute CreateArtistSongRequest r) { return service.createSong(userId(a),r); }
    @GetMapping("/songs") public List<ArtistContentResponse> songs(Authentication a) { return service.songs(userId(a)); }
    @GetMapping("/songs/{id}") public ArtistContentResponse song(Authentication a,@PathVariable Long id) { return service.song(userId(a),id); }
    @PutMapping(value="/songs/{id}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ArtistContentResponse updateSong(Authentication a,@PathVariable Long id,@Valid @ModelAttribute UpdateArtistSongRequest r) { return service.updateSong(userId(a),id,r); }
    @DeleteMapping("/songs/{id}") public ResponseEntity<Void> deleteSong(Authentication a,@PathVariable Long id) { service.deleteSong(userId(a),id); return ResponseEntity.noContent().build(); }
    @PatchMapping("/songs/{id}/submit") public ArtistContentResponse submitSong(Authentication a,@PathVariable Long id) { return service.submitSong(userId(a),id); }
}
