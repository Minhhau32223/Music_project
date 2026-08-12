package com.spotify.spotify_backend.domain.repository;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryRepository {

    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String publicId);
}