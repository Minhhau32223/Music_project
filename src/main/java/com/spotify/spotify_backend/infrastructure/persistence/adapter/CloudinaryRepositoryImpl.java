package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.io.File;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CloudinaryRepositoryImpl implements CloudinaryRepository {

   private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {

        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            Map result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto",
                            "public_id", file.getOriginalFilename() + "_" + System.currentTimeMillis()
                    )
            );

            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
}
