package com.spotify.spotify_backend.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spotify.spotify_backend.application.dto.Request.SongQuery;
import com.spotify.spotify_backend.application.dto.Response.PageResult;
import com.spotify.spotify_backend.domain.model.Song;
public interface SongRepository {
    Song save(Song song);
    Optional<Song> findById(Long id);
    List<Song> findAll();
    List<Song> findByAlbumId(Long albumId);
    void deleteById(Long id);
    List<Song> findByNameContaining(String keyword);
    Optional<Song> findPublishedById(Long id);
    List<Song> findAllPublished();
    List<Song> findPublishedByAlbumId(Long albumId);
    List<Song> findPublishedByNameContaining(String keyword);
    List<Song> findTopSongs(int limit);
    List<Song> findNewReleases(int limit);
    List<Song> findRecommendedSongs(List<Long> artistIds, List<Long> albumIds, List<Long> excludedSongIds, int limit);
    PageResult<Song> findSongsByFilters(SongQuery query);
    List<Song> findTopSongsExcluding(List<Long> excludedSongIds, int limit);
}
