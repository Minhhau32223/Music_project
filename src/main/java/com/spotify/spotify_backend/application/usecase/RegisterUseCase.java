package    com.spotify.spotify_backend.application.usecase;

import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
public interface RegisterUseCase {
    RegisterResponse register(RegisterRequest request);
}