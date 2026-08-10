package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.songusecase.GetSongUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import com.spotify.spotify_backend.domain.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSongUseCaseImpl implements GetSongUseCase {
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    @Override
    public SongResponse execute(Long id) {

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        Album album =albumRepository.findById(song.getAlbumId())
                                      .orElseThrow(() -> new NotFoundException("Album not found"));;
        Artist artist = artistRepository.findById(song.getArtistId())
                                        .orElseThrow(() -> new NotFoundException("Artist not found"));
        return SongMapper.toResponse(song, album.getName(),  artist.getName());
    }
}
