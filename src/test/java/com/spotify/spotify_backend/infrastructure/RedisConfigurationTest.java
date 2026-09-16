package com.spotify.spotify_backend.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.infrastructure.config.RedisConfig;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import static org.assertj.core.api.Assertions.assertThat;

class RedisConfigurationTest {
    @Test
    @SuppressWarnings("unchecked")
    void ttlAndJsonSerializationPreserveSongResponseType() {
        var redisConfig = new RedisConfig();
        var mapper = new ObjectMapper();
        var top = redisConfig.topSongsConfiguration(mapper);
        var releases = redisConfig.newReleasesConfiguration(mapper);
        assertThat(top.getTtl()).isEqualTo(Duration.ofMinutes(5));
        assertThat(releases.getTtl()).isEqualTo(Duration.ofMinutes(10));
        var value = List.of(new SongResponse(1L, "song", "file", "image", "lyrics", "180",
                10L, 2L, 3L, "album", "artist"));
        var serializer = (SerializationPair<Object>)
                top.getValueSerializationPair();
        var restored = serializer.read(serializer.write(value));
        assertThat(restored).isEqualTo(value);
        assertThat(((List<?>) restored).getFirst()).isInstanceOf(SongResponse.class);
    }
}
