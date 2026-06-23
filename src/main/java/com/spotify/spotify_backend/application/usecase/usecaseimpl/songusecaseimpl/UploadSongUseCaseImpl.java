    package com.spotify.spotify_backend.application.usecase.usecaseimpl.songusecaseimpl;

    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import com.spotify.spotify_backend.application.dto.Request.UploadSongRequest;
    import com.spotify.spotify_backend.application.dto.Response.UploadSongResponse;
    import com.spotify.spotify_backend.application.usecase.songusecase.UploadSongUseCase;
    import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

    import lombok.RequiredArgsConstructor;

    @Service
    @RequiredArgsConstructor
    public class UploadSongUseCaseImpl implements UploadSongUseCase {

        private final CloudinaryRepository cloudinaryRepository;

        @Override
        public UploadSongResponse execute(UploadSongRequest request) {

            String fileUrl = upload(request.file(), "songs");
            String imageUrl = upload(request.image(), "song-images");

            return new UploadSongResponse(fileUrl, imageUrl);
        }

    private String upload(MultipartFile multipartFile, String folder) {

    try { 

        String url = cloudinaryRepository.uploadFile(multipartFile, folder);

        return url;

    } catch (Exception e) {
        throw new RuntimeException("Upload error: " + e.getMessage());
    }
}
    }