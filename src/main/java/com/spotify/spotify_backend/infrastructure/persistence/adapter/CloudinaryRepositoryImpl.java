package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import com.spotify.spotify_backend.domain.model.AudioUpload;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.spotify.spotify_backend.domain.exception.FileUploadException;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CloudinaryRepositoryImpl implements CloudinaryRepository {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        return upload(file, folder, false).get("secure_url").toString();
    }
    @Override
    public AudioUpload uploadAudio(MultipartFile file, String folder) {
        Map result = upload(file, folder, true);
        Object value = result.get("duration");
        if (!(value instanceof Number n) || !Double.isFinite(n.doubleValue()) || n.doubleValue() <= 0) {
            throw new FileUploadException("Audio duration could not be determined");
        }
        long seconds = Math.max(1, Math.round(((Number) value).doubleValue()));
        return new AudioUpload(result.get("secure_url").toString(), String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60));
    }
    private Map upload(MultipartFile file, String folder, boolean audio) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".upload");
            file.transferTo(tempFile);

            Map result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", audio ? "video" : "auto",
                            "public_id", UUID.randomUUID().toString()
                    )
            );

            return result;

        } catch (Exception e) {
            log.error("Cloudinary upload failed ({})", e.getClass().getSimpleName());
            throw new FileUploadException("Upload failed");
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (IOException e) {
                    log.warn("Could not remove temporary upload file ({})", e.getClass().getSimpleName());
                }
            }
        }
    }

    @Override
    public void deleteFile(String publicId) {

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.error("Cloudinary deletion failed ({})", e.getClass().getSimpleName());
            throw new FileUploadException("Delete failed");
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
