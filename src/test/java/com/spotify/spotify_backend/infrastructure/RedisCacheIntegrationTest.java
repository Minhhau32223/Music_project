package com.spotify.spotify_backend.infrastructure;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetNewReleasesUseCase;
import com.spotify.spotify_backend.application.usecase.recommendationusecase.GetTopSongsUseCase;
import com.spotify.spotify_backend.domain.model.*;
import com.spotify.spotify_backend.domain.repository.*;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@EnabledIfEnvironmentVariable(named = "TEST_REDIS_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RedisCacheIntegrationTest extends BaseIntegrationTest {
    @DynamicPropertySource
    static void redis(DynamicPropertyRegistry registry) {
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.data.redis.host", () -> System.getenv("TEST_REDIS_HOST"));
        registry.add("spring.data.redis.port", () -> System.getenv().getOrDefault("TEST_REDIS_PORT", "6379"));
    }

    @Autowired GetTopSongsUseCase top;
    @Autowired GetNewReleasesUseCase releases;
    @MockitoSpyBean SongRepository songs;
    @Autowired AlbumRepository albums;
    @Autowired ArtistRepository artists;
    @Autowired CacheManager caches;
    @Autowired StringRedisTemplate redis;
    @Autowired PlatformTransactionManager transactions;

    @Test
    void redisHitTtlMutationsAndRollbackRespectCacheConsistency() {
        caches.getCache("topSongs").clear();
        caches.getCache("newReleases").clear();
        var artist = new Artist();
        artist.setName("cache artist"); artist.setBio("bio"); artist.setProfileImage("image");
        artist = artists.save(artist);
        var album = new Album();
        album.setName("cache album"); album.setDescription("desc"); album.setImage("image"); album.setBgColor("black");
        album = albums.save(album);
        var song = songs.save(Song.builder().name("cache song").filePath("file").imagePath("image")
                .lyrics("lyrics").duration("180").playCount(10L)
                .albumId(album.getId()).artistId(artist.getId()).build());
        Long extraSongId = null;
        try {
            var first = top.execute(3);
            assertThat(top.execute(3)).isEqualTo(first);
            verify(songs, times(1)).findTopSongs(3);
            assertThat(redis.getExpire("spotify:v1:topSongs::3", TimeUnit.SECONDS)).isBetween(1L, 300L);
            releases.execute(3);
            releases.execute(3);
            verify(songs, times(1)).findNewReleases(3);
            assertThat(redis.getExpire("spotify:v1:newReleases::3", TimeUnit.SECONDS)).isBetween(1L, 600L);

            album.setName("renamed album");
            albums.save(album);
            assertThat(top.execute(3).getFirst().albumName()).isEqualTo("renamed album");
            assertThat(caches.getCache("newReleases").get(3)).isNull();
            artist.setName("renamed artist");
            artists.save(artist);
            assertThat(top.execute(3).getFirst().artistName()).isEqualTo("renamed artist");

            song.setPlayCount(200L);
            songs.save(song);
            assertThat(top.execute(3).getFirst().playCount()).isEqualTo(200L);
            Long songId = song.getId();
            new TransactionTemplate(transactions).executeWithoutResult(status -> {
                var rollbackSong = songs.findById(songId).orElseThrow();
                rollbackSong.setName("must rollback");
                songs.save(rollbackSong);
                status.setRollbackOnly();
            });
            assertThat(caches.getCache("topSongs").get(3)).isNotNull();
            assertThat(top.execute(3).getFirst().name()).isEqualTo("cache song");

            var extra = songs.save(Song.builder().name("new song").filePath("file").imagePath("image")
                    .lyrics("lyrics").duration("180").playCount(50L)
                    .albumId(album.getId()).artistId(artist.getId()).build());
            extraSongId = extra.getId();
            assertThat(top.execute(3)).hasSize(2);
            assertThat(releases.execute(3).getFirst().id()).isEqualTo(extraSongId);
            songs.deleteById(extraSongId);
            extraSongId = null;
            assertThat(top.execute(3)).hasSize(1);
            assertThat(releases.execute(3)).hasSize(1);
        } finally {
            if (extraSongId != null) songs.deleteById(extraSongId);
            songs.deleteById(song.getId());
            albums.deleteById(album.getId());
            artists.deleteById(artist.getId());
            caches.getCache("topSongs").clear();
            caches.getCache("newReleases").clear();
        }
    }
}

