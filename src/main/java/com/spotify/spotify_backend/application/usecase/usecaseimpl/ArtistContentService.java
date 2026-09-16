package com.spotify.spotify_backend.application.usecase.usecaseimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spotify.spotify_backend.application.dto.Request.CreateArtistAlbumRequest;
import com.spotify.spotify_backend.application.dto.Request.CreateArtistSongRequest;
import com.spotify.spotify_backend.application.dto.Request.RejectContentRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateArtistAlbumRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateArtistSongRequest;
import com.spotify.spotify_backend.application.dto.Response.ArtistContentResponse;
import com.spotify.spotify_backend.application.dto.Response.ArtistResponse;
import com.spotify.spotify_backend.domain.exception.BadRequestException;
import com.spotify.spotify_backend.domain.exception.ConflictException;
import com.spotify.spotify_backend.domain.exception.ForbiddenException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;
import lombok.RequiredArgsConstructor;

/** Creator workflow. The actor is always resolved from the JWT user id. */
@Service
@RequiredArgsConstructor
public class ArtistContentService {
    private static final String DRAFT = "DRAFT";
    private static final String PENDING = "PENDING_REVIEW";
    private static final String PUBLISHED = "PUBLISHED";
    private static final String REJECTED = "REJECTED";
    private static final String DISABLED = "DISABLED";

    private final ArtistJpaRepository artists;
    private final AlbumJpaRepository albums;
    private final SongJpaRepository songs;
    private final CloudinaryRepository cloudinary;

    @Transactional(readOnly = true)
    public ArtistResponse profile(Long userId) { return artistResponse(currentArtist(userId)); }

    @Transactional
    public ArtistResponse updateProfile(Long userId, String name, String bio, String profileImage) {
        ArtistEntity artist = currentArtist(userId);
        artist.setName(name); artist.setBio(bio); artist.setProfileImage(profileImage);
        return artistResponse(artists.save(artist));
    }

    @Transactional
    public ArtistContentResponse createAlbum(Long userId, CreateArtistAlbumRequest request) {
        ArtistEntity artist = currentArtist(userId);
        String image = uploadImage(request.image(), "albums/" + artist.getId());
        AlbumEntity album = AlbumEntity.builder().artistId(artist.getId()).name(request.name())
                .image(image).description(request.description()).bgColor(request.bgColor()).status(DRAFT)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        return albumResponse(albums.save(album));
    }

    @Transactional(readOnly = true)
    public List<ArtistContentResponse> albums(Long userId) {
        ArtistEntity artist = currentArtist(userId);
        return albums.findByArtistIdOrderByCreatedAtDesc(artist.getId()).stream().map(this::albumResponse).toList();
    }

    @Transactional
    public ArtistContentResponse updateAlbum(Long userId, Long albumId, UpdateArtistAlbumRequest request) {
        AlbumEntity album = ownedAlbum(userId, albumId); editable(album.getStatus());
        album.setName(request.name()); album.setDescription(request.description()); album.setBgColor(request.bgColor());
        if (hasFile(request.image())) album.setImage(uploadImage(request.image(), "albums/" + album.getArtistId()));
        album.setStatus(DRAFT); album.setRejectionReason(null); album.setUpdatedAt(LocalDateTime.now());
        return albumResponse(albums.save(album));
    }

    @Transactional
    public void deleteAlbum(Long userId, Long albumId) {
        AlbumEntity album = ownedAlbum(userId, albumId);
        if (!DRAFT.equals(album.getStatus()) && !REJECTED.equals(album.getStatus())) throw new ConflictException("Only draft or rejected albums can be deleted");
        albums.delete(album);
    }

    @Transactional
    public ArtistContentResponse submitAlbum(Long userId, Long albumId) {
        AlbumEntity album = ownedAlbum(userId, albumId); submit(album.getStatus());
        album.setStatus(PENDING); album.setRejectionReason(null); album.setUpdatedAt(LocalDateTime.now());
        return albumResponse(albums.save(album));
    }

    @Transactional
    public ArtistContentResponse createSong(Long userId, CreateArtistSongRequest request) {
        ArtistEntity artist = currentArtist(userId); AlbumEntity album = albumForArtist(artist.getId(), request.albumId());
        SongEntity song = SongEntity.builder().name(request.name()).file(uploadAudio(request.file(), "songs/audio/" + artist.getId()))
                .image(uploadImage(request.image(), "songs/images/" + artist.getId())).lyrics(request.lyrics())
                .duration(request.duration()).playCount(0L).albumId(album.getId()).artistId(artist.getId())
                .status(DRAFT).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        return songResponse(songs.save(song));
    }

    @Transactional(readOnly = true)
    public List<ArtistContentResponse> songs(Long userId) {
        ArtistEntity artist = currentArtist(userId);
        return songs.findByArtistIdOrderByCreatedAtDesc(artist.getId()).stream().map(this::songResponse).toList();
    }

    @Transactional(readOnly = true)
    public ArtistContentResponse song(Long userId, Long songId) { return songResponse(ownedSong(userId, songId)); }

    @Transactional
    public ArtistContentResponse updateSong(Long userId, Long songId, UpdateArtistSongRequest request) {
        SongEntity song = ownedSong(userId, songId); editable(song.getStatus()); albumForArtist(song.getArtistId(), request.albumId());
        song.setName(request.name()); song.setLyrics(request.lyrics()); song.setDuration(request.duration()); song.setAlbumId(request.albumId());
        if (hasFile(request.file())) song.setFile(uploadAudio(request.file(), "songs/audio/" + song.getArtistId()));
        if (hasFile(request.image())) song.setImage(uploadImage(request.image(), "songs/images/" + song.getArtistId()));
        song.setStatus(DRAFT); song.setRejectionReason(null); song.setUpdatedAt(LocalDateTime.now());
        return songResponse(songs.save(song));
    }

    @Transactional
    public void deleteSong(Long userId, Long songId) {
        SongEntity song = ownedSong(userId, songId);
        if (!DRAFT.equals(song.getStatus()) && !REJECTED.equals(song.getStatus())) throw new ConflictException("Only draft or rejected songs can be deleted");
        songs.delete(song);
    }

    @Transactional
    public ArtistContentResponse submitSong(Long userId, Long songId) {
        SongEntity song = ownedSong(userId, songId); submit(song.getStatus());
        song.setStatus(PENDING); song.setRejectionReason(null); song.setUpdatedAt(LocalDateTime.now());
        return songResponse(songs.save(song));
    }

    @Transactional(readOnly = true)
    public List<ArtistContentResponse> pendingSongs() { return songs.findByStatusOrderByCreatedAtAsc(PENDING).stream().map(this::songResponse).toList(); }
    @Transactional(readOnly = true)
    public List<ArtistContentResponse> pendingAlbums() { return albums.findByStatusOrderByCreatedAtAsc(PENDING).stream().map(this::albumResponse).toList(); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse approveSong(Long id) { SongEntity s = pendingSong(id); s.setStatus(PUBLISHED); s.setPublishedAt(LocalDateTime.now()); s.setUpdatedAt(LocalDateTime.now()); return songResponse(songs.save(s)); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse rejectSong(Long id, RejectContentRequest r) { SongEntity s = pendingSong(id); s.setStatus(REJECTED); s.setRejectionReason(r.rejectionReason()); s.setUpdatedAt(LocalDateTime.now()); return songResponse(songs.save(s)); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse disableSong(Long id) { SongEntity s = requiredSong(id); if (!PUBLISHED.equals(s.getStatus())) throw new ConflictException("Only published songs can be disabled"); s.setStatus(DISABLED); s.setUpdatedAt(LocalDateTime.now()); return songResponse(songs.save(s)); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse approveAlbum(Long id) { AlbumEntity a = pendingAlbum(id); a.setStatus(PUBLISHED); a.setPublishedAt(LocalDateTime.now()); a.setUpdatedAt(LocalDateTime.now()); return albumResponse(albums.save(a)); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse rejectAlbum(Long id, RejectContentRequest r) { AlbumEntity a = pendingAlbum(id); a.setStatus(REJECTED); a.setRejectionReason(r.rejectionReason()); a.setUpdatedAt(LocalDateTime.now()); return albumResponse(albums.save(a)); }
    @Transactional @CacheEvict(cacheNames={"topSongs","newReleases","songDetail","albumDetail"}, allEntries=true) public ArtistContentResponse disableAlbum(Long id) { AlbumEntity a = requiredAlbum(id); if (!PUBLISHED.equals(a.getStatus())) throw new ConflictException("Only published albums can be disabled"); a.setStatus(DISABLED); a.setUpdatedAt(LocalDateTime.now()); return albumResponse(albums.save(a)); }

    private ArtistEntity currentArtist(Long userId) { return artists.findByUserId(userId).orElseThrow(() -> new ForbiddenException("Artist profile is required")); }
    private AlbumEntity ownedAlbum(Long userId, Long id) { ArtistEntity a=currentArtist(userId); return albumForArtist(a.getId(), id); }
    private AlbumEntity albumForArtist(Long artistId, Long id) { AlbumEntity a=requiredAlbum(id); if (!artistId.equals(a.getArtistId())) throw new ForbiddenException("You do not own this album"); return a; }
    private SongEntity ownedSong(Long userId, Long id) { ArtistEntity a=currentArtist(userId); SongEntity s=requiredSong(id); if (!a.getId().equals(s.getArtistId())) throw new ForbiddenException("You do not own this song"); return s; }
    private SongEntity requiredSong(Long id) { return songs.findById(id).orElseThrow(() -> new NotFoundException("Song not found")); }
    private AlbumEntity requiredAlbum(Long id) { return albums.findById(id).orElseThrow(() -> new NotFoundException("Album not found")); }
    private SongEntity pendingSong(Long id) { SongEntity s=requiredSong(id); if(!PENDING.equals(s.getStatus())) throw new ConflictException("Song is not pending review"); return s; }
    private AlbumEntity pendingAlbum(Long id) { AlbumEntity a=requiredAlbum(id); if(!PENDING.equals(a.getStatus())) throw new ConflictException("Album is not pending review"); return a; }
    private void editable(String status) { if (!DRAFT.equals(status) && !REJECTED.equals(status)) throw new ConflictException("Only draft or rejected content can be edited"); }
    private void submit(String status) { if (!DRAFT.equals(status) && !REJECTED.equals(status)) throw new ConflictException("Only draft or rejected content can be submitted"); }
    private boolean hasFile(MultipartFile f) { return f != null && !f.isEmpty(); }
    private String uploadAudio(MultipartFile f, String folder) { if(!hasFile(f) || f.getContentType()==null || !f.getContentType().startsWith("audio/")) throw new BadRequestException("Song file must be audio"); return cloudinary.uploadFile(f, folder); }
    private String uploadImage(MultipartFile f, String folder) { if(!hasFile(f)) throw new BadRequestException("Cover image is required"); if(f.getContentType()==null || !f.getContentType().startsWith("image/")) throw new BadRequestException("Cover must be an image"); return cloudinary.uploadFile(f, folder); }
    private ArtistResponse artistResponse(ArtistEntity a) { return new ArtistResponse(a.getId(),a.getName(),a.getBio(),a.getProfileImage()); }
    private ArtistContentResponse songResponse(SongEntity s) { return new ArtistContentResponse(s.getId(),s.getName(),s.getArtistId(),s.getAlbumId(),s.getStatus(),s.getRejectionReason(),s.getCreatedAt(),s.getUpdatedAt(),s.getPublishedAt(),s.getFile(),s.getImage(),s.getLyrics(),s.getDuration()); }
    private ArtistContentResponse albumResponse(AlbumEntity a) { return new ArtistContentResponse(a.getId(),a.getName(),a.getArtistId(),null,a.getStatus(),a.getRejectionReason(),a.getCreatedAt(),a.getUpdatedAt(),a.getPublishedAt(),null,a.getImage(),null,null); }
}
