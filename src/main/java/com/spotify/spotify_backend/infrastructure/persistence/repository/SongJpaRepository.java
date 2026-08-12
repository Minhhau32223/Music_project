package com.spotify.spotify_backend.infrastructure.persistence.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongJpaRepository extends JpaRepository<SongEntity, Long> {
    Optional<SongEntity> findById(Long id);
    List<SongEntity> findByAlbumId(Long albumId);
    List<SongEntity> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT s FROM SongEntity s WHERE (:artistId IS NULL OR s.artistId = :artistId) AND (:albumId IS NULL OR s.albumId = :albumId)")
    Page<SongEntity> findSongsByFilters(
            @Param("artistId") Long artistId,
            @Param("albumId") Long albumId,
            Pageable pageable
    );

    @Query("SELECT s FROM SongEntity s ORDER BY s.playCount DESC")
    List<SongEntity> findTopSongs(Pageable pageable);

    @Query("SELECT s FROM SongEntity s ORDER BY s.createdAt DESC")
    List<SongEntity> findNewReleases(Pageable pageable);

    @Query("SELECT s FROM SongEntity s WHERE (s.artistId IN :artistIds OR s.albumId IN :albumIds) AND (COALESCE(:excludedSongIds, NULL) IS NULL OR s.id NOT IN :excludedSongIds)")
    List<SongEntity> findRecommendedSongs(
            @Param("artistIds") List<Long> artistIds,
            @Param("albumIds") List<Long> albumIds,
            @Param("excludedSongIds") List<Long> excludedSongIds,
            Pageable pageable
    );
}
