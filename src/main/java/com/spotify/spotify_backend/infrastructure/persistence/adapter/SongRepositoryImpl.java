package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.application.dto.Request.SongQuery;
import com.spotify.spotify_backend.application.dto.Response.PageResult;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.SongRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SongRepositoryImpl implements SongRepository {

    private static final String PUBLISHED = "PUBLISHED";

    private final SongJpaRepository songJpaRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
    public Song save(Song song) {

        SongEntity entity = song.getId() == null
                ? SongEntity.builder().status(PUBLISHED).createdAt(song.getCreatedAt()).build()
                : songJpaRepository.findById(song.getId()).orElseThrow();
        entity.setName(song.getName());
        entity.setFile(song.getFilePath());
        entity.setImage(song.getImagePath());
        entity.setLyrics(song.getLyrics());
        entity.setDuration(song.getDuration());
        entity.setPlayCount(song.getPlayCount());
        entity.setAlbumId(song.getAlbumId());
        entity.setArtistId(song.getArtistId());

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
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
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
    public Optional<Song> findPublishedById(Long id) {
        return songJpaRepository.findByIdAndStatus(id, PUBLISHED).map(this::mapToDomain);
    }

    @Override
    public List<Song> findAllPublished() {
        return songJpaRepository.findByStatus(PUBLISHED).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<Song> findPublishedByAlbumId(Long albumId) {
        return songJpaRepository.findByAlbumIdAndStatus(albumId, PUBLISHED)
                .stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<Song> findPublishedByNameContaining(String keyword) {
        return songJpaRepository.findByNameContainingIgnoreCaseAndStatus(keyword, PUBLISHED)
                .stream().map(this::mapToDomain).toList();
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
        List<Long> excluded = (excludedSongIds == null || excludedSongIds.isEmpty()) ? List.of(-1L) : excludedSongIds;

        Pageable pageable = PageRequest.of(0, limit);
        return songJpaRepository.findRecommendedSongs(artists, albums, excluded, pageable)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public PageResult<Song> findSongsByFilters(SongQuery request) {
        Sort sort = Sort.by(request.descending() ? Sort.Direction.DESC : Sort.Direction.ASC, request.sortBy());
        if (!request.sortBy().equals("id")) {
            sort = sort.and(Sort.by("id"));
        }
        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);
        Page<SongEntity> entityPage =
                songJpaRepository.findSongsByFilters(request.artistId(), request.albumId(), pageable);

        List<Song> songs = entityPage.getContent().stream()
                .map(this::mapToDomain)
                .toList();

        return new PageResult<>(
                songs,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast()
        );
    }

    @Override
    public List<Song> findTopSongsExcluding(List<Long> excludedSongIds, int limit) {
        var excluded = excludedSongIds.isEmpty() ? List.of(-1L) : excludedSongIds;
        return songJpaRepository.findTopSongsExcluding(excluded, PageRequest.of(0, limit))
                .stream().map(this::mapToDomain).toList();
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
