package com.spotify.spotify_backend.presentation.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.spotify.spotify_backend.application.dto.Request.CreateArtistApplicationRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistApplicationResponse;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.ArtistApplicationService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/api/artist-applications") @RequiredArgsConstructor
public class ArtistApplicationController {
 private final ArtistApplicationService service;
 @PostMapping public ArtistApplicationResponse apply(Authentication authentication,@Valid @RequestBody CreateArtistApplicationRequest request){return service.apply((Long)authentication.getPrincipal(),request);}
 @GetMapping("/me") public List<ArtistApplicationResponse> mine(Authentication authentication){return service.mine((Long)authentication.getPrincipal());}
}
