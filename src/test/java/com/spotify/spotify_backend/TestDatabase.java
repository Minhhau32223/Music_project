package com.spotify.spotify_backend;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.io.IOException;

/** A disposable real PostgreSQL server; CI may supply a dedicated database instead. */
public final class TestDatabase {
    private static EmbeddedPostgres postgres;

    private TestDatabase() {}

    public static synchronized void configure(DynamicPropertyRegistry registry) {
        String url = System.getenv("TEST_DB_URL");
        if (url != null && !url.isBlank()) {
            registry.add("spring.datasource.url", () -> url);
            registry.add("spring.datasource.username", () -> System.getenv().getOrDefault("TEST_DB_USERNAME", "postgres"));
            registry.add("spring.datasource.password", () -> System.getenv().getOrDefault("TEST_DB_PASSWORD", ""));
            return;
        }
        try {
            if (postgres == null) {
                postgres = EmbeddedPostgres.builder().setPort(0).start();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot start disposable PostgreSQL for tests", ex);
        }
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "");
    }
}
