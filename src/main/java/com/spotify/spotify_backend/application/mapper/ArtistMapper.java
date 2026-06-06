package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.domain.model.Artist;

public class ArtistMapper {

    private ArtistMapper() {}

    public static Artist toDomain(CreateArtistRequest request) {

        Artist artist = new Artist();

        artist.setName(request.name());
        artist.setBio(request.bio());
        artist.setProfileImage(request.profileImage());

        return artist;
    }

    public static ArtistResponse toResponse(Artist artist) {

        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getBio(),
                artist.getProfileImage()
        );
    }
}