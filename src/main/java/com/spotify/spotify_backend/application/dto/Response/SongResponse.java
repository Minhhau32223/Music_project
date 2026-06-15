package com.spotify.spotify_backend.application.dto.Response;

public record SongResponse(
    Long id,
    String name,
    String file,
    String image,
    String lyrics,
    String duration,
    Long playCount,
    Long albumId,
    Long artistId,
    String albumName,
    String artistName
) {

    
    
}
