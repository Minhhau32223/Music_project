package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

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
                entity.getArtistId()
        );
    }
}