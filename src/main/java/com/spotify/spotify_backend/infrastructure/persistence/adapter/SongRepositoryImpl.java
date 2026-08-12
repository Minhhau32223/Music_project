package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.application.dto.Request.SongFilterRequest;
import com.spotify.spotify_backend.application.dto.Response.PageResponse;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SongRepositoryImpl implements SongRepository {

    private final SongJpaRepository songJpaRepository;

    @Override
    public Song save(Song song) {

        SongEntity entity = SongEntity.builder()
                .id(song.getId())
                .name(song.getName())
                .file(song.getFilePath())
                .image(song.getImagePath())
                .lyrics(song.getLyrics())
                .duration(song.getDuration())
                .playCount(song.getPlayCount())
                .albumId(song.getAlbumId())
                .artistId(song.getArtistId())
                .createdAt(song.getCreatedAt())
                .build();

        SongEntity savedEntity = songJpaRepository.save(entity);

        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songJpaRepository
                .findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Song> findAll() {
        return songJpaRepository
                .findAll()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByAlbumId(Long albumId) {
        return songJpaRepository
                .findByAlbumId(albumId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        songJpaRepository.deleteById(id);
    }
    @Override
    public List<Song> findByNameContaining(
            String keyword
    ) {

        return songJpaRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Song> findTopSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songJpaRepository.findTopSongs(pageable)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Song> findNewReleases(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songJpaRepository.findNewReleases(pageable)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Song> findRecommendedSongs(List<Long> artistIds, List<Long> albumIds, List<Long> excludedSongIds, int limit) {
        List<Long> artists = (artistIds == null || artistIds.isEmpty()) ? List.of(-1L) : artistIds;
        List<Long> albums = (albumIds == null || albumIds.isEmpty()) ? List.of(-1L) : albumIds;
        List<Long> excluded = (excludedSongIds == null || excludedSongIds.isEmpty()) ? null : excludedSongIds;

        Pageable pageable = PageRequest.of(0, limit);
        return songJpaRepository.findRecommendedSongs(artists, albums, excluded, pageable)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public PageResponse<Song> findSongsByFilters(SongFilterRequest request) {
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 10;

        Sort sort = Sort.unsorted();
        if (request.sortBy() != null && !request.sortBy().isBlank()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (request.sortDirection() != null && request.sortDirection().equalsIgnoreCase("DESC")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, request.sortBy());
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SongEntity> entityPage =
                songJpaRepository.findSongsByFilters(request.artistId(), request.albumId(), pageable);

        List<Song> songs = entityPage.getContent().stream()
                .map(this::mapToDomain)
                .toList();

        return new PageResponse<>(
                songs,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast()
        );
    }

    private Song mapToDomain(SongEntity entity) {
        return new Song(
                entity.getId(),
                entity.getName(),
                entity.getFile(),
                entity.getImage(),
                entity.getLyrics(),
                entity.getDuration(),
                entity.getPlayCount(),
                entity.getAlbumId(),
                entity.getArtistId(),
                entity.getCreatedAt()
        );
    }
}