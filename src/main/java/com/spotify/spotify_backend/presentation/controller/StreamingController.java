package com.spotify.spotify_backend.presentation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.StreamingResponse;
import com.spotify.spotify_backend.application.usecase.songusecase.StreamSongUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class StreamingController {
    private final StreamSongUseCase streamSongUseCase;

    // Trả về JSON metadata (cho frontend cần biết URL + Type)
    @GetMapping("/song/{id}")
    public ResponseEntity<StreamingResponse> getStreamInfo(@PathVariable Long id) {
        StreamingResponse response = streamSongUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    // Direct redirect cho browser <audio src="..."> tự động phát
    @GetMapping("/song/{id}/play")
    public ResponseEntity<Void> playSongDirectly(@PathVariable Long id) {
        StreamingResponse response = streamSongUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, response.streamUrl())
                .build();
    }
}

