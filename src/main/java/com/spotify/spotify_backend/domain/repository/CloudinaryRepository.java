package com.spotify.spotify_backend.domain.repository;

import org.springframework.web.multipart.MultipartFile;
import com.spotify.spotify_backend.domain.model.AudioUpload;

public interface CloudinaryRepository {

    AudioUpload uploadAudio(MultipartFile file, String folder);

    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String publicId);
}
