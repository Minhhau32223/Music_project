package com.spotify.spotify_backend.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spotify.spotify_backend.application.dto.Request.UploadSongRequest;
import com.spotify.spotify_backend.application.dto.Response.UploadSongResponse;
import com.spotify.spotify_backend.application.usecase.songusecase.UploadSongUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongUploadController {

    private final UploadSongUseCase uploadSongUseCase;

    @PostMapping("/upload")
    public UploadSongResponse uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("image") MultipartFile image
    ) {

        UploadSongRequest request = new UploadSongRequest(file, image);
        return uploadSongUseCase.execute(request);
    }
}