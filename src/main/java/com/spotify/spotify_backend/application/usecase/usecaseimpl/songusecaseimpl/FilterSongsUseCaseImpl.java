package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.SongFilterRequest;
import com.spotify.spotify_backend.application.dto.Request.SongQuery;
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

        int page = request.page() == null ? 0 : request.page();
        int size = request.size() == null ? 10 : request.size();
        String sortBy = request.sortBy() == null ? "id" : request.sortBy();
        String direction = request.sortDirection() == null ? "ASC" : request.sortDirection();
        if (page < 0 || size < 1 || size > 100 || (long) page * size > Integer.MAX_VALUE) {
            throw new BadRequestException("page must be non-negative, size between 1 and 100, and offset within integer range");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field. Allowed fields: " + ALLOWED_SORT_FIELDS);
        }
        if (!Set.of("ASC", "DESC").contains(direction)) {
            throw new BadRequestException("sortDirection must be ASC or DESC");
        }
        var query = new SongQuery(
                request.artistId(), request.albumId(), sortBy, direction.equals("DESC"), page, size);
        var domainPage = songRepository.findSongsByFilters(query);

        List<SongResponse> content = domainPage.content().stream()
                .map(song -> {
                    Artist artist = artistRepository.findById(song.getArtistId()).orElse(null);
                    Album album = albumRepository.findById(song.getAlbumId()).orElse(null);
                    return SongMapper.toResponse(
                            song,
                            album != null ? album.getName() : "",
                            artist != null ? artist.getName() : ""
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
