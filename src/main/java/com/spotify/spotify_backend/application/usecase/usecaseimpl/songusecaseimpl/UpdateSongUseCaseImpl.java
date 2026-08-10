package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.UpdateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.songusecase.UpdateSongUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateSongUseCaseImpl implements UpdateSongUseCase {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public SongResponse execute(Long songId, UpdateSongRequest request) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        Album album = albumRepository.findById(request.albumId())
                .orElseThrow(() -> new NotFoundException("Album not found"));

        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        SongMapper.updateDomain(song, request);

        if (request.file() != null && !request.file().isEmpty()) {
            String songUrl = cloudinaryRepository.uploadFile(request.file(), "songs");
            song.setFilePath(songUrl);
        }

        if (request.image() != null && !request.image().isEmpty()) {
            String imageUrl = cloudinaryRepository.uploadFile(request.image(), "song-images");
            song.setImagePath(imageUrl);
        }

        Song saved = songRepository.save(song);

        return SongMapper.toResponse(
                saved,
                album.getName(),
                artist.getName()
        );
    }
}
