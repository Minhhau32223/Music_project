package com.spotify.spotify_backend.infrastructure.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spotify.spotify_backend.domain.model.AudioUpload;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.UserEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Imports an externally mounted, licensed no-copyright media folder when explicitly enabled. */
@Component
@ConditionalOnProperty(prefix = "app.seed.media", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class MediaSeedRunner implements ApplicationRunner {
    private static final String ARTIST_NAME = "Music App Library";
    private static final String ALBUM_NAME = "No Copyright Music";
    private static final String FALLBACK_IMAGE = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80";

    private final UserJpaRepository users;
    private final ArtistJpaRepository artists;
    private final AlbumJpaRepository albums;
    private final SongJpaRepository songs;
    private final CloudinaryRepository cloudinary;

    @Value("${app.seed.media.directory:}")
    private String mediaDirectory;

    @Value("${app.seed.media.image-directory:}")
    private String imageDirectory;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (mediaDirectory == null || mediaDirectory.isBlank()) {
            log.warn("Media seed is enabled but MEDIA_SEED_DIR is empty; no media imported");
            return;
        }

        Path directory = Path.of(mediaDirectory);
        if (!Files.isDirectory(directory)) {
            log.warn("Media seed directory does not exist: {}", directory);
            return;
        }

        UserEntity owner = users.findByEmail("admin@music.local").orElse(null);
        if (owner == null) {
            log.warn("Media seed skipped because admin seed user does not exist");
            return;
        }
        ArtistEntity artist = artists.findByName(ARTIST_NAME).orElseGet(() -> artists.save(ArtistEntity.builder()
                .userId(owner.getId()).name(ARTIST_NAME).bio("Royalty-free demonstration catalog for Music App.").profileImage(FALLBACK_IMAGE).build()));
        AlbumEntity album = albums.findByName(ALBUM_NAME).orElseGet(() -> albums.save(AlbumEntity.builder()
                .artistId(artist.getId()).name(ALBUM_NAME).image(resolveCover()).description("Licensed no-copyright demonstration tracks")
                .bgColor("#1f1b2e").status("PUBLISHED").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now()).build()));

        try (Stream<Path> paths = Files.list(directory)) {
            paths.filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".mp3"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .forEach(path -> importSong(path, artist, album));
        } catch (IOException exception) {
            log.warn("Could not scan media seed directory: {}", exception.getMessage());
        }
    }

    private void importSong(Path path, ArtistEntity artist, AlbumEntity album) {
        String name = displayName(path.getFileName().toString());
        boolean exists = songs.findByNameContainingIgnoreCase(name).stream().anyMatch(song -> name.equals(song.getName()));
        if (exists) return;
        try {
            AudioUpload upload = cloudinary.uploadAudio(new PathMultipartFile(path), "music-app/seed");
            LocalDateTime now = LocalDateTime.now();
            songs.save(SongEntity.builder().name(name).file(upload.url()).image(album.getImage())
                    .lyrics("Instrumental demonstration track: " + name).duration(upload.duration()).playCount(0L)
                    .albumId(album.getId()).artistId(artist.getId()).status("PUBLISHED")
                    .createdAt(now).updatedAt(now).publishedAt(now).build());
            log.info("Imported media seed track: {}", name);
        } catch (RuntimeException exception) {
            log.warn("Skipping media seed file {}: {}", path.getFileName(), exception.getMessage());
        }
    }

    private String resolveCover() {
        if (imageDirectory == null || imageDirectory.isBlank()) return FALLBACK_IMAGE;
        Path directory = Path.of(imageDirectory);
        try (Stream<Path> paths = Files.list(directory)) {
            Path image = paths.filter(path -> List.of(".jpg", ".jpeg", ".png", ".webp").stream()
                    .anyMatch(extension -> path.toString().toLowerCase(Locale.ROOT).endsWith(extension)))
                    .sorted().findFirst().orElse(null);
            return image == null ? FALLBACK_IMAGE : cloudinary.uploadFile(new PathMultipartFile(image), "music-app/seed/covers");
        } catch (Exception exception) {
            log.warn("Using fallback cover image: {}", exception.getMessage());
            return FALLBACK_IMAGE;
        }
    }

    private static String displayName(String filename) {
        String withoutExtension = filename.replaceFirst("(?i)\\.mp3$", "");
        return withoutExtension.replaceAll("[_-]+", " ").replaceAll("\\s+", " ").trim();
    }

    private static final class PathMultipartFile implements MultipartFile {
        private final Path path;

        private PathMultipartFile(Path path) { this.path = path; }
        @Override public String getName() { return path.getFileName().toString(); }
        @Override public String getOriginalFilename() { return path.getFileName().toString(); }
        @Override public String getContentType() { return path.toString().toLowerCase(Locale.ROOT).endsWith(".mp3") ? "audio/mpeg" : "image/jpeg"; }
        @Override public boolean isEmpty() { try { return Files.size(path) == 0; } catch (IOException e) { return true; } }
        @Override public long getSize() { try { return Files.size(path); } catch (IOException e) { return 0; } }
        @Override public byte[] getBytes() throws IOException { return Files.readAllBytes(path); }
        @Override public InputStream getInputStream() throws IOException { return Files.newInputStream(path); }
        @Override public void transferTo(java.io.File destination) throws IOException { Files.copy(path, destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
        @Override public void transferTo(Path destination) throws IOException { Files.copy(path, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
    }
}
