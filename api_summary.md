# Tổng hợp REST APIs (Sau Phase 3)

> **Lưu ý đồng bộ:** Tài liệu này là bản tổng hợp lịch sử. Contract dùng cho local frontend integration là [API Contract V1](docs/integration/api_contract_v1.md); inventory đầy đủ và gap register nằm trong [docs/integration](docs/integration/). Contract V1 bao gồm refresh/logout, Artist workflow và Admin moderation.

Dưới đây là danh sách toàn bộ các API hiện tại của backend, được gom nhóm theo từng chức năng, bao gồm các tính năng mới bổ sung trong Phase 1, Phase 2, và Phase 3.

---

## 1. Xác thực (Authentication)
Xử lý đăng ký, đăng nhập và cấp phát JWT.
- `POST /api/auth/register/` : Đăng ký tài khoản mới.
- `POST /api/auth/login/` : Đăng nhập & Nhận JWT.

---

## 2. Quản lý Người dùng (Users)
Quản lý thông tin profile, avatar, và trạng thái tài khoản.
- `GET /api/users/me/` : Lấy thông tin user hiện tại (Yêu cầu JWT).
- `PUT /api/users/profile/` : Cập nhật thông tin cơ bản (name, gender, etc).
- `PUT /api/users/avatar/` : Upload / Cập nhật ảnh đại diện (Multipart, Upload Cloudinary).
- `PUT /api/users/change-password/` : Đổi mật khẩu.
- `DELETE /api/users/me/` : Vô hiệu hóa tài khoản (Soft delete).

---

## 3. Bài hát (Songs) & Streaming
Quản lý nhạc, upload file, nghe nhạc, và tìm kiếm.
- `GET /api/songs/` : Lấy toàn bộ bài hát.
- `GET /api/songs/{id}/` : Lấy chi tiết một bài hát.
- `POST /api/songs/create/` : Tạo mới bài hát + Upload MP3 & Cover Image (Multipart).
- `DELETE /api/songs/{id}/` : Xóa bài hát.

**Streaming & Nhạc (Phase 2 & Phase 3)**:
- `GET /api/stream/song/{id}` : Lấy Streaming Info (JSON gồm URL Cloudinary & Metadata).
- `GET /api/stream/song/{id}/play` : Direct 302 Redirect (Cho trình duyệt phát trực tiếp file MP3).
- `GET /api/songs/filter` : Lọc & Tìm kiếm nâng cao có phân trang (theo `artistId`, `albumId`, sắp xếp `playCount`, `createdAt`, `id`, `name`).

---

## 4. Gợi ý nhạc (Recommendations) — (Phase 3)
Các thuật toán gợi ý nhạc đa dạng.
- `GET /api/recommendations/top-songs` : Lấy danh sách Top bảng xếp hạng (playCount cao).
- `GET /api/recommendations/new-releases` : Lấy danh sách bài hát mới phát hành (createdAt giảm dần).
- `GET /api/recommendations/personalized` : Gợi ý cá nhân hóa dựa trên lịch sử nghe (Yêu cầu JWT).

---

## 5. Lịch sử nghe (Recently Played)
- `POST /api/recently-played/play` : Xác nhận nghe 1 bài hát, tự động tăng `playCount` và ghi log (Yêu cầu JWT).
- `GET /api/recently-played/me` : Lấy lịch sử các bài đã nghe của user hiện tại.

---

## 6. Nghệ sĩ (Artists)
- `GET /api/artists/` : Lấy toàn bộ nghệ sĩ.
- `GET /api/artists/{id}/` : Lấy chi tiết nghệ sĩ.
- `POST /api/artists/create/` : Tạo mới nghệ sĩ.
- `PUT /api/artists/{id}/` : Cập nhật nghệ sĩ (Hỗ trợ upload ảnh mới qua Multipart).
- `DELETE /api/artists/{id}/` : Xóa nghệ sĩ.
- `POST /api/artist-follows/{artistId}` : Theo dõi nghệ sĩ.
- `DELETE /api/artist-follows/{artistId}` : Hủy theo dõi nghệ sĩ.
- `GET /api/artist-follows/me` : Lấy danh sách nghệ sĩ user đang theo dõi.

---

## 7. Album (Albums)
- `GET /api/albums/` : Lấy toàn bộ album.
- `GET /api/albums/{id}/` : Lấy chi tiết album.
- `POST /api/albums/create/` : Tạo mới album.
- `PUT /api/albums/{id}/` : Cập nhật album (Hỗ trợ upload Cover Image mới).
- `DELETE /api/albums/{id}/` : Xóa album.
- `GET /api/albums/{id}/songs/` : Lấy danh sách bài hát thuộc album này.

---

## 8. Danh sách phát (Playlists)
- `GET /api/playlists/` : Lấy toàn bộ playlist.
- `GET /api/playlists/{id}` : Lấy chi tiết playlist.
- `POST /api/playlists/create` : Tạo mới playlist với name/image; chủ sở hữu lấy từ JWT.
- `PUT /api/playlists/{id}` : Cập nhật playlist của chính user trong JWT.
- `DELETE /api/playlists/{id}` : Xóa playlist của chính user trong JWT.
- `GET /api/playlists/{playlistId}/songs` : Lấy các bài hát trong playlist.
- `POST /api/playlists/{playlistId}/songs` : Thêm bài hát vào playlist.
- `DELETE /api/playlists/{playlistId}/songs/{songId}` : Xóa bài hát khỏi playlist.

---

## 9. Bài hát yêu thích (Favorites)
- `GET /api/favorites/me` : Lấy danh sách bài hát yêu thích của User.
- `POST /api/favorites/{songId}` : Thích một bài hát.
- `DELETE /api/favorites/{songId}` : Bỏ thích bài hát.

---

## 10. Tìm kiếm cơ bản (Search)
- `GET /api/search?keyword=...` : Tìm kiếm text thuần túy (Artist, Album, Song).
