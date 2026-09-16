package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {
    private static final String PUBLISHED = "PUBLISHED";
    private final AlbumJpaRepository albumJpaRepository;
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
    public Album save(Album album){
        AlbumEntity entity = album.getId() == null
                ? AlbumEntity.builder().status(PUBLISHED).build()
                : albumJpaRepository.findById(album.getId()).orElseThrow();
        entity.setName(album.getName());
        entity.setImage(album.getImage());
        entity.setDescription(album.getDescription());
        entity.setBgColor(album.getBgColor());
        AlbumEntity saveEntity= albumJpaRepository.save(entity);
        return mapToDomain(saveEntity);

    }
   

    @Override
    public Optional<Album> findById(Long id) {
        return albumJpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Album> findAll() {
        return albumJpaRepository.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
    public void deleteById(Long id) {
        albumJpaRepository.deleteById(id);
    }
    @Override
    public List<Album> findByNameContaining(
            String keyword
    ) {

        return albumJpaRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Album> findPublishedById(Long id) {
        return albumJpaRepository.findByIdAndStatus(id, PUBLISHED).map(this::mapToDomain);
    }

    @Override
    public List<Album> findAllPublished() {
        return albumJpaRepository.findByStatus(PUBLISHED).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<Album> findPublishedByNameContaining(String keyword) {
        return albumJpaRepository.findByNameContainingIgnoreCaseAndStatus(keyword, PUBLISHED)
                .stream().map(this::mapToDomain).toList();
    }
    private Album mapToDomain(AlbumEntity entity) {

        Album album = new Album();

        album.setId(entity.getId());
        album.setName(entity.getName());
        album.setImage(entity.getImage());
        album.setDescription(entity.getDescription());
        album.setBgColor(entity.getBgColor());

        return album;
    }


}
