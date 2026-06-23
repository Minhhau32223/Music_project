package com.spotify.spotify_backend.presentation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.usecase.songusecase.StreamSongUseCase;
import com.spotify.spotify_backend.domain.model.Song;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
public class StreamingController {
    private final StreamSongUseCase streamSongUseCase;

   @GetMapping("/song/{id}")
public ResponseEntity<Void> streamSong(@PathVariable Long id) {

    Song song = streamSongUseCase.execute(id);

    return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, song.getFilePath())
            .build();
         
}
}

