package com.spotify.spotify_backend.application.usecase.usecaseimpl;
import java.time.LocalDateTime; import java.util.List;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import com.spotify.spotify_backend.application.dto.Request.*; import com.spotify.spotify_backend.application.dto.Response.ArtistApplicationResponse;
import com.spotify.spotify_backend.domain.exception.*; import com.spotify.spotify_backend.domain.model.*; import com.spotify.spotify_backend.infrastructure.persistence.entity.*; import com.spotify.spotify_backend.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
@Service @RequiredArgsConstructor public class ArtistApplicationService {
 private final ArtistApplicationJpaRepository applications; private final UserJpaRepository users; private final ArtistJpaRepository artists;
 @Transactional public ArtistApplicationResponse apply(Long userId, CreateArtistApplicationRequest request){
  UserEntity user=users.findById(userId).orElseThrow(()->new NotFoundException("User not found"));
  if(user.getRole()==Role.ARTIST) throw new ConflictException("Artist cannot apply again");
  if(applications.existsByUserIdAndStatus(userId,ArtistApplicationStatus.PENDING)) throw new ConflictException("A pending artist application already exists");
  return response(applications.save(ArtistApplicationEntity.builder().userId(userId).artistName(request.artistName()).bio(request.bio()).profileImage(request.profileImage()).status(ArtistApplicationStatus.PENDING).build())); }
 public List<ArtistApplicationResponse> mine(Long userId){return applications.findByUserIdOrderBySubmittedAtDesc(userId).stream().map(this::response).toList();}
 public List<ArtistApplicationResponse> pending(){return applications.findByStatusOrderBySubmittedAtAsc(ArtistApplicationStatus.PENDING).stream().map(this::response).toList();}
 public ArtistApplicationResponse get(Long id){return response(applications.findById(id).orElseThrow(()->new NotFoundException("Artist application not found")));}
 @Transactional public ArtistApplicationResponse approve(Long id,Long adminId){ ArtistApplicationEntity app=pending(id); UserEntity user=users.findById(app.getUserId()).orElseThrow(()->new NotFoundException("User not found"));
  if(artists.findByUserId(user.getId()).isEmpty()) artists.save(ArtistEntity.builder().userId(user.getId()).name(app.getArtistName()).bio(app.getBio()).profileImage(app.getProfileImage()).build());
  user.setRole(Role.ARTIST); users.save(user); app.setStatus(ArtistApplicationStatus.APPROVED); app.setReviewedAt(LocalDateTime.now()); app.setReviewedBy(adminId); return response(applications.save(app)); }
 @Transactional public ArtistApplicationResponse reject(Long id,Long adminId,RejectArtistApplicationRequest request){ArtistApplicationEntity app=pending(id); app.setStatus(ArtistApplicationStatus.REJECTED);app.setReviewedAt(LocalDateTime.now());app.setReviewedBy(adminId);app.setRejectionReason(request.rejectionReason());return response(applications.save(app));}
 private ArtistApplicationEntity pending(Long id){ArtistApplicationEntity app=applications.findById(id).orElseThrow(()->new NotFoundException("Artist application not found"));if(app.getStatus()!=ArtistApplicationStatus.PENDING)throw new ConflictException("Artist application has already been reviewed");return app;}
 private ArtistApplicationResponse response(ArtistApplicationEntity e){return new ArtistApplicationResponse(e.getId(),e.getUserId(),e.getArtistName(),e.getBio(),e.getProfileImage(),e.getStatus(),e.getSubmittedAt(),e.getReviewedAt(),e.getReviewedBy(),e.getRejectionReason());}
}
