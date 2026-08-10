package com.spotify.spotify_backend.application.usecase.usecaseimpl.artistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.UpdateArtistRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.usecase.artistusecase.UpdateArtistUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateArtistUseCaseImpl implements UpdateArtistUseCase {

    private final ArtistRepository artistRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public ArtistResponse execute(Long artistId, UpdateArtistRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        if (request.name() != null && !request.name().trim().isEmpty()) {
            artist.setName(request.name());
        }
        if (request.bio() != null) {
            artist.setBio(request.bio());
        }

        if (request.profileImage() != null && !request.profileImage().isEmpty()) {
            String imageUrl = cloudinaryRepository.uploadFile(request.profileImage(), "artist-images");
            artist.setProfileImage(imageUrl);
        }

        Artist savedArtist = artistRepository.save(artist);

        return ArtistMapper.toResponse(savedArtist);
    }
}
