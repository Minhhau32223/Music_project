package com.spotify.spotify_backend.application.dto.Response;

import java.util.List;

public  record SearchResponse(
        List<SongResponse> songs,
        List<AlbumResponse> albums,
        List<ArtistResponse> artists
) {
}
