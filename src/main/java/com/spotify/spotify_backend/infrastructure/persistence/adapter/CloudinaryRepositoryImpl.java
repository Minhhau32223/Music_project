package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.io.File;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.spotify.spotify_backend.domain.exception.FileUploadException;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CloudinaryRepositoryImpl implements CloudinaryRepository {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {

        try {

            File tempFile = File.createTempFile(
                    "upload-",
                    file.getOriginalFilename()
            );
            file.transferTo(tempFile);

            Map result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto",
                            "public_id", file.getOriginalFilename()
                                    + "_"
                                    + System.currentTimeMillis()
                    )
            );

            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new FileUploadException("Upload failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String publicId) {

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new FileUploadException("Delete failed: " + e.getMessage());
        }
    }

    /**
     * Trích xuất publicId từ Cloudinary secure_url.
     * Ví dụ URL:
     * https://res.cloudinary.com/{cloud}/image/upload/v123/{folder}/{name}.jpg
     * → publicId = {folder}/{name}
     */
    public static String extractPublicId(String secureUrl) {

        if (secureUrl == null || secureUrl.isBlank()) {
            return null;
        }

        String marker = "/upload/";
        int uploadIndex = secureUrl.indexOf(marker);

        if (uploadIndex == -1) {
            return null;
        }

        String afterUpload = secureUrl.substring(uploadIndex + marker.length());

        // Bỏ version prefix (v1234567890/) nếu có
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }

        // Bỏ extension
        int dotIndex = afterUpload.lastIndexOf(".");
        if (dotIndex != -1) {
            afterUpload = afterUpload.substring(0, dotIndex);
        }

        return afterUpload;
    }
}
