package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.SongFilterRequest;
import com.spotify.spotify_backend.application.dto.Response.PageResponse;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.searchusecase.FilterSongsUseCase;
import com.spotify.spotify_backend.domain.exception.BadRequestException;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilterSongsUseCaseImpl implements FilterSongsUseCase {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("playCount", "id", "createdAt", "name");

    @Override
    public PageResponse<SongResponse> execute(SongFilterRequest request) {

        // Validate sortBy
        if (request.sortBy() != null && !request.sortBy().isBlank()) {
            if (!ALLOWED_SORT_FIELDS.contains(request.sortBy())) {
                throw new BadRequestException("Invalid sort field. Allowed fields: " + ALLOWED_SORT_FIELDS);
            }
        }

        PageResponse<Song> domainPage = songRepository.findSongsByFilters(request);

        List<SongResponse> content = domainPage.content().stream()
                .map(song -> {
                    Artist artist = artistRepository.findById(song.getArtistId()).orElse(null);
                    Album album = albumRepository.findById(song.getAlbumId()).orElse(null);
                    return SongMapper.toResponse(
                            song,
                            artist != null ? artist.getName() : "",
                            album != null ? album.getName() : ""
                    );
                })
                .toList();

        return new PageResponse<>(
                content,
                domainPage.pageNumber(),
                domainPage.pageSize(),
                domainPage.totalElements(),
                domainPage.totalPages(),
                domainPage.last()
        );
    }
}
