package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.CreateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.songusecase.CreateSongUseCase;
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
public class CreateSongUseCaseImpl implements CreateSongUseCase {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public SongResponse execute(CreateSongRequest request) {


        Album album = albumRepository.findById(request.albumId())
                .orElseThrow(() -> new RuntimeException("Album not found"));

        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        String songUrl = cloudinaryRepository.uploadFile(request.file(), "songs");
        String imageUrl = cloudinaryRepository.uploadFile(request.image(), "song-images");

        Song song = new Song();
        song.setName(request.name());
        song.setFilePath(songUrl);
        song.setImagePath(imageUrl);
        song.setDuration(request.duration());
        song.setPlayCount(request.playCount());
        song.setLyrics(request.lyrics());
        song.setAlbumId(album.getId());
        song.setArtistId(artist.getId());
        Song saved = songRepository.save(song);

        return SongMapper.toResponse(
                saved,
                album.getName(),
                artist.getName()
        );
    }
}