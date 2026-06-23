package com.spotify.spotify_backend.application.dto.Response;

public record UploadSongResponse(
    String fileUrl,
    String imageUrl
) {

}
