package com.spotify.spotify_backend.presentation.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.spotify.spotify_backend.application.dto.Request.RejectArtistApplicationRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistApplicationResponse;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.ArtistApplicationService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/api/admin/artist-applications") @RequiredArgsConstructor
public class AdminArtistApplicationController {
 private final ArtistApplicationService service;
 @GetMapping public List<ArtistApplicationResponse> pending(){return service.pending();}
 @GetMapping("/{id}") public ArtistApplicationResponse get(@PathVariable Long id){return service.get(id);}
 @PatchMapping("/{id}/approve") public ArtistApplicationResponse approve(@PathVariable Long id,Authentication authentication){return service.approve(id,(Long)authentication.getPrincipal());}
 @PatchMapping("/{id}/reject") public ArtistApplicationResponse reject(@PathVariable Long id,Authentication authentication,@Valid @RequestBody RejectArtistApplicationRequest request){return service.reject(id,(Long)authentication.getPrincipal(),request);}
}
