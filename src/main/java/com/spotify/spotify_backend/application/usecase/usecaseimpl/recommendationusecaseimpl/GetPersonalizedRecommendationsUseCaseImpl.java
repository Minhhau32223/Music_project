package com.spotify.spotify_backend.application.usecase.usecaseimpl.recommendationusecaseimpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetPersonalizedRecommendationsUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.model.RecentlyPlayed;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.RecentlyPlayedRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPersonalizedRecommendationsUseCaseImpl implements GetPersonalizedRecommendationsUseCase {

    private final RecentlyPlayedRepository recentlyPlayedRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Override
    public List<SongResponse> execute(Long userId, int limit) {

        List<RecentlyPlayed> history = recentlyPlayedRepository.findByUserId(userId);

        if (history.isEmpty()) {
            // Nếu không có lịch sử, trả về top songs làm fallback
            return songRepository.findTopSongs(limit).stream()
                    .map(song -> {
                        Artist artist = artistRepository.findById(song.getArtistId()).orElse(null);
                        Album album = albumRepository.findById(song.getAlbumId()).orElse(null);
                        return SongMapper.toResponse(song, artist != null ? artist.getName() : "", album != null ? album.getName() : "");
                    })
                    .toList();
        }

        // Lấy danh sách excluded song IDs
        List<Long> excludedSongIds = history.stream()
                .map(RecentlyPlayed::getSongId)
                .distinct()
                .toList();

        // Map songId -> Song để tìm Artist & Album tương ứng từ các bài đã nghe
        List<Song> playedSongs = history.stream()
                .map(hp -> songRepository.findById(hp.getSongId()).orElse(null))
                .filter(s -> s != null)
                .toList();

        // Phân tích Top Artists & Top Albums
        List<Long> topArtistIds = getTopIds(playedSongs.stream().map(Song::getArtistId).toList(), 3);
        List<Long> topAlbumIds = getTopIds(playedSongs.stream().map(Song::getAlbumId).toList(), 3);

        // Lấy các bài hát được gợi ý từ Repository
        List<Song> recommendedSongs = songRepository.findRecommendedSongs(topArtistIds, topAlbumIds, excludedSongIds, limit);

        // Fallback thêm top songs nếu số bài hát gợi ý ít hơn limit mong muốn
        if (recommendedSongs.size() < limit) {
            List<Long> currentRecIds = recommendedSongs.stream().map(Song::getId).toList();
            List<Song> fallbackSongs = songRepository.findTopSongs(limit * 2).stream()
                    .filter(s -> !excludedSongIds.contains(s.getId()))
                    .filter(s -> !currentRecIds.contains(s.getId()))
                    .limit(limit - recommendedSongs.size())
                    .toList();
            recommendedSongs = new java.util.ArrayList<>(recommendedSongs);
            recommendedSongs.addAll(fallbackSongs);
        }

        return recommendedSongs.stream()
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
    }

    private List<Long> getTopIds(List<Long> ids, int limit) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
