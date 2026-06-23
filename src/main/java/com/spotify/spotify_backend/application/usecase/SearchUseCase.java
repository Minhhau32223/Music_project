package com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.application.dto.Response.SearchResponse;

public interface SearchUseCase {

    SearchResponse execute(
            String keyword
    );

}