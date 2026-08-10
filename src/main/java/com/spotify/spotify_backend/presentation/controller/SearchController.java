package com.spotify.spotify_backend.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Response.SearchResponse;
import com.spotify.spotify_backend.application.usecase.SearchUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchUseCase searchUseCase;

    @GetMapping
    public SearchResponse search(

            @RequestParam String keyword

    ) {

        return searchUseCase.execute(

                keyword

        );

    }

}