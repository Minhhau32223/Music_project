package com.spotify.spotify_backend.application.usecase.usecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.application.dto.Response.SearchResponse;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.AlbumMapper;
import com.spotify.spotify_backend.application.mapper.ArtistMapper;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.SearchUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchUseCaseImpl
        implements SearchUseCase {

    private final SongRepository songRepository;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    @Override
    public SearchResponse execute(
            String keyword
    ) {

        List<SongResponse> songs =
                songRepository
                        .findByNameContaining(keyword)
                        .stream()
                        .map(song -> {

                            Artist artist =
                                    artistRepository
                                            .findById(song.getArtistId())
                                            .orElse(null);

                            Album album =
                                    albumRepository
                                            .findById(song.getAlbumId())
                                            .orElse(null);

                            return SongMapper.toResponse(

                                    song,

                                    artist != null
                                            ? artist.getName()
                                            : "",

                                    album != null
                                            ? album.getName()
                                            : ""

                            );

                        })
                        .toList();

        List<AlbumResponse> albums =
                albumRepository
                        .findByNameContaining(keyword)
                        .stream()
                        .map(AlbumMapper::toResponse)
                        .toList();

        List<ArtistResponse> artists =
                artistRepository
                        .findByNameContaining(keyword)
                        .stream()
                        .map(ArtistMapper::toResponse)
                        .toList();

        return new SearchResponse(

                songs,

                albums,

                artists

        );

    }

}