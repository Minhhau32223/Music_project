package com.spotify.spotify_backend.application.usecase.songusecase;

import com.spotify.spotify_backend.domain.model.Song;

public interface StreamSongUseCase {
    Song execute(Long songId);

}
