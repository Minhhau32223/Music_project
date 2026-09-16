package com.spotify.spotify_backend.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory, ObjectMapper mapper) {
        var topSongs = topSongsConfiguration(mapper);
        var newReleases = newReleasesConfiguration(mapper);
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(
                        factory, BatchStrategies.scan(1000)))
                .cacheDefaults(topSongs)
                .withInitialCacheConfigurations(Map.of(
                        "topSongs", topSongs,
                        "newReleases", newReleases))
                .disableCreateOnMissingCache()
                .transactionAware()
                .build();
    }

    public RedisCacheConfiguration topSongsConfiguration(ObjectMapper mapper) {
        return baseConfiguration(mapper).entryTtl(Duration.ofMinutes(5));
    }

    public RedisCacheConfiguration newReleasesConfiguration(ObjectMapper mapper) {
        return baseConfiguration(mapper).entryTtl(Duration.ofMinutes(10));
    }

    private RedisCacheConfiguration baseConfiguration(ObjectMapper mapper) {
        // Both caches contain a concrete list type; no unsafe polymorphic deserialization.
        var type = mapper.getTypeFactory().constructCollectionType(List.class, SongResponse.class);
        var serializer = new Jackson2JsonRedisSerializer<List<SongResponse>>(mapper, type);
        return RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("spotify:v1:")
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(serializer));
    }
}
