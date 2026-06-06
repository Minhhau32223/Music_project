package com.spotify.spotify_backend.application.usecase.songusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;


public interface GetAllSongsUseCase {
     List<SongResponse> execute();
}
