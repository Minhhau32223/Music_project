# Tổng hợp API backend dành cho frontend

Ngày đối chiếu source: **15/09/2026**. Tài liệu mô tả các controller, DTO, cấu hình bảo mật và logic service hiện có trong repository; không coi các API trong roadmap là đã triển khai. Không chạy lại các thao tác ghi dữ liệu để lập tài liệu này.

## 1. Kết nối và quy ước chung

- Tiền tố API: `/api`. Các bảng dưới đây ghi đường dẫn đầy đủ, giữ đúng dấu `/` cuối vì backend chưa hỗ trợ mọi biến thể.
- Port mặc định trong source và Docker Compose: `8000`. Phiên import album trước dùng `http://localhost:8001`; frontend cần đặt base URL theo port đang chạy, không hardcode port cho mọi môi trường.
- Frontend gọi HTTP API; không kết nối trực tiếp PostgreSQL. Media được trả dưới dạng URL Cloudinary.
- CORS mặc định cho `http://localhost:3000` và `http://localhost:5173`, có credentials; có thể đổi bằng `CORS_ALLOWED_ORIGINS`. `localhost` và `127.0.0.1` là hai origin khác nhau.
- JSON: `Content-Type: application/json`. Upload: dùng `FormData`, không tự đặt header `Content-Type` để trình duyệt sinh multipart boundary.
- Giới hạn cấu hình hiện tại: 50 MB/file và 50 MB/toàn request multipart, bao gồm audio và ảnh.
- Thành công trả trực tiếp object hoặc array; **không có envelope chung** `{data, success, message}`.
- Mặc định HTTP thành công là `200`. Những API `204`, `302` được ghi riêng. `200 rỗng` nghĩa là không gọi `response.json()`.
- `Public`: không cần token. `Auth`: tài khoản đăng nhập. `Owner`: có token và sở hữu đối tượng. `ARTIST`/`ADMIN`: role tương ứng. Backend hiện không mặc định cho ADMIN truy cập route chỉ dành cho ARTIST.

```http
Authorization: Bearer <accessToken>
```

## 2. Đăng ký, đăng nhập và phiên làm việc

| Method | Endpoint | Quyền | Dữ liệu gửi | Kết quả và nhiệm vụ frontend |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/register/` | Public | JSON `username, email, password` | `RegisterResponse`; tạo tài khoản, sau đó chuyển sang đăng nhập |
| POST | `/api/auth/login/` | Public | JSON `email, password` | `LoginResponse` và cookie refresh; lưu access token, lấy hồ sơ hiện tại |
| POST | `/api/auth/refresh/` | Public + cookie | Không body; gửi cookie | `{accessToken}`; khôi phục phiên hoặc đổi access token hết hạn |
| POST | `/api/auth/logout/` | Public | Không body; gửi cookie | `204`; xóa cookie refresh; frontend xóa token, user và dữ liệu riêng tư trong cache |

Validation: đăng ký yêu cầu username, email hợp lệ, password tối thiểu 6 ký tự. Login password dài 6–100 ký tự. Đổi mật khẩu có quy tắc riêng ở phần tài khoản.

Refresh cookie tên `nocturne_refresh`, HttpOnly, path `/api/auth`; mặc định SameSite=Lax, Secure=false trong cấu hình local. Frontend không đọc cookie bằng JavaScript và không gửi refresh token trong JSON. Dùng `credentials: 'include'` hoặc Axios `withCredentials: true` cho auth.

Luồng tích hợp:

1. Login → nhận accessToken → `GET /api/users/me/` để lấy role và avatar. Login response không có role.
2. Khi mở lại ứng dụng, thử refresh cookie rồi lấy `/api/users/me/`; không có phiên thì hiển thị guest.
3. Request cần đăng nhập trả 401 → refresh **một lần**, retry request một lần; gom các request refresh đồng thời thành một promise. Không áp dụng vòng refresh cho login/register/refresh.
4. Refresh thất bại → xóa phiên frontend. 403 là thiếu quyền, không refresh lặp.
5. Sau khi đơn nghệ sĩ được duyệt, refresh token rồi tải lại hồ sơ: quyền trong access token cũ chưa tự thay đổi.

Token Bearer sai/hết hạn vẫn có thể gây 401 trên route public do JWT filter kiểm tra header trước. Khi ở chế độ guest, bỏ header Bearer cũ.

Logout hiện xóa cookie, không phải API thu hồi mọi access token đã phát hành.

## 3. Tài khoản cá nhân

| Method | Endpoint | Quyền | Dữ liệu gửi | Kết quả / nhiệm vụ |
| --- | --- | --- | --- | --- |
| GET | `/api/users/me/` | Auth | — | `UserProfileResponse`; khởi tạo session, điều hướng theo role |
| PUT | `/api/users/profile/` | Auth | JSON `username, email` | `UserProfileResponse`; chỉnh tên và email, cả hai bắt buộc |
| PUT | `/api/users/avatar/` | Auth | FormData `avatar: File` | `UserProfileResponse`; thay ảnh đại diện người dùng |
| PUT | `/api/users/change-password/` | Auth | JSON `currentPassword, newPassword` | `200 rỗng`; mật khẩu mới ít nhất 8 ký tự |
| DELETE | `/api/users/me/` | Auth | — | `204`; vô hiệu hóa tài khoản; frontend kết thúc phiên |

## 4. Catalog bài hát, album và nghệ sĩ

Tất cả GET trong bảng này là Public.

| Method | Endpoint | Query | Response | Nhiệm vụ frontend |
| --- | --- | --- | --- | --- |
| GET | `/api/songs/` | — | `SongResponse[]` | Danh sách toàn bộ bài public; ưu tiên filter cho danh sách lớn |
| GET | `/api/songs/{id}/` | — | `SongResponse` | Chi tiết bài, lyrics, tên album/nghệ sĩ |
| GET | `/api/songs/filter` | `artistId, albumId, sortBy, sortDirection, page, size` | `PageResponse<SongResponse>` | Phân trang, lọc theo nghệ sĩ/album, sắp xếp |
| GET | `/api/albums/` | — | `AlbumResponse[]` | Danh sách album |
| GET | `/api/albums/{id}/` | — | `AlbumResponse` | Header trang album, ảnh, mô tả, màu nền |
| GET | `/api/albums/{id}/songs/` | — | `SongResponse[]` | Track list, hàng đợi phát album |
| GET | `/api/artists/` | — | `ArtistResponse[]` | Danh sách nghệ sĩ |
| GET | `/api/artists/{id}/` | — | `ArtistResponse` | Tên, bio và avatar nghệ sĩ |

Filter dùng `page` bắt đầu từ 0 (mặc định 0), `size` từ 1 đến 100 (mặc định 10), `sortBy` thuộc `id`, `name`, `playCount`, `createdAt` (mặc định `id`), `sortDirection` đúng chữ hoa `ASC` hoặc `DESC` (mặc định `ASC`). Sai giá trị trả 400. Không có query `keyword` ở API filter.

```text
/api/songs/filter?artistId=2&page=0&size=20&sortBy=playCount&sortDirection=DESC
/api/songs/filter?albumId=2&page=0&size=20
```

Catalog bài hát/album công khai được lọc theo trạng thái phát hành trong logic đọc. Nội dung draft/rejected dùng API nghệ sĩ ở phần 9.

## 5. Tìm kiếm và gợi ý

| Method | Endpoint | Quyền | Query | Response / nhiệm vụ |
| --- | --- | --- | --- | --- |
| GET | `/api/search` | Public | `keyword` bắt buộc | `SearchResponse`; tìm bài hát, album, nghệ sĩ cùng lúc |
| GET | `/api/recommendations/top-songs` | Public | `limit=10` | `SongResponse[]`; danh sách bài nổi bật |
| GET | `/api/recommendations/new-releases` | Public | `limit=10` | `SongResponse[]`; bài mới |
| GET | `/api/recommendations/personalized` | Auth | `limit=10` | `SongResponse[]`; gợi ý theo lịch sử, có fallback bài nổi bật |

`limit` hợp lệ từ 1 đến 100. Search chưa có phân trang. Frontend nên debounce ô tìm kiếm, encode query bằng `URLSearchParams`, hủy/bỏ qua response cũ nếu người dùng đổi từ khóa.

## 6. Player và lịch sử nghe

| Method | Endpoint | Quyền | Dữ liệu gửi | Response / nhiệm vụ |
| --- | --- | --- | --- | --- |
| GET | `/api/stream/song/{id}` | Public | — | `StreamingResponse`; lấy URL media và contentType |
| GET | `/api/stream/song/{id}/play` | Public | — | `302` với header `Location`; dùng trực tiếp làm `audio.src` |
| POST | `/api/recently-played/play` | Auth | JSON `{songId}` | `200 rỗng`; tăng playCount và ghi lịch sử |
| GET | `/api/recently-played/me` | Auth | — | `RecentlyPlayedResponse[]`; mục nghe gần đây |

```ts
audio.src = `${API_ORIGIN}/api/stream/song/${song.id}/play`;
await audio.play();
// Sau khi playback thực sự bắt đầu, gửi { songId: song.id } nếu đã đăng nhập.
```

GET stream không tự ghi lịch sử. Frontend cần chặn gửi trùng theo một lượt nghe; service hiện tăng đếm mỗi lần POST, không có idempotency key hoặc ngưỡng nghe tối thiểu. Không gửi ở mỗi `timeupdate` hay mỗi lần resume. Guest vẫn phát nhạc nhưng không gọi API ghi lịch sử.

Play/pause, seek, volume, repeat, shuffle, queue là trạng thái frontend. Backend chưa có API đồng bộ các trạng thái này. Duration trong DTO là chuỗi, không phải số giây; dữ liệu import trước có `00:00`, nên lấy `audio.duration` sau `loadedmetadata` khi cần hiển thị thời lượng thực. Lyrics là text thường, giữ xuống dòng; chưa có hợp đồng timestamp karaoke/LRC.

## 7. Playlist, yêu thích và theo dõi

| Method | Endpoint | Quyền | Dữ liệu gửi | Response / nhiệm vụ |
| --- | --- | --- | --- | --- |
| GET | `/api/playlists/` | Public | — | `PlaylistResponse[]`; danh sách playlist |
| GET | `/api/playlists/{id}` | Public | — | `PlaylistResponse`; header playlist |
| POST | `/api/playlists/create` | Auth | JSON `name` bắt buộc, `image` URL tùy chọn | `PlaylistResponse`; tạo playlist cho user từ token |
| PUT | `/api/playlists/{id}` | Owner | FormData `name`, `image: File` | `PlaylistResponse`; đổi tên/ảnh |
| DELETE | `/api/playlists/{id}` | Owner | — | `200 rỗng`; xóa playlist |
| GET | `/api/playlists/{playlistId}/songs` | Public | — | `SongResponse[]`; track list |
| POST | `/api/playlists/{playlistId}/songs` | Owner | JSON `{songId}`; ID dương | `200 rỗng`; thêm bài |
| DELETE | `/api/playlists/{playlistId}/songs/{songId}` | Owner | — | `200 rỗng`; bỏ bài khỏi playlist |
| GET | `/api/favorites/me` | Auth | — | `SongResponse[]`; bài đã thích |
| POST | `/api/favorites/{songId}` | Auth | — | `200 rỗng`; thích bài |
| DELETE | `/api/favorites/{songId}` | Auth | — | `200 rỗng`; bỏ thích |
| GET | `/api/artist-follows/me` | Auth | — | `ArtistResponse[]`; nghệ sĩ đang theo dõi |
| POST | `/api/artist-follows/{artistId}` | Auth | — | `200 rỗng`; theo dõi nghệ sĩ |
| DELETE | `/api/artist-follows/{artistId}` | Auth | — | `200 rỗng`; bỏ theo dõi |

Nhóm artist-follows hỗ trợ thêm dấu `/` cuối. Không tự thêm dấu `/` cho các nhóm khác.

Hiện chưa có `/api/playlists/me`. Để dựng thư viện cá nhân, lọc kết quả `/api/playlists/` bằng `playlist.userId === currentUser.id`. Đây là lọc hiển thị; các GET playlist hiện công khai, không phải chức năng playlist riêng tư. Không gửi `userId` khi tạo/sửa playlist.

Tạo Set từ các ID trong favorites/follows để hiển thị nút đang thích/đang theo dõi. Sau mutation, cập nhật hoặc invalidate cache liên quan; nếu optimistic update thất bại thì rollback.

## 8. Đăng ký trở thành nghệ sĩ

| Method | Endpoint | Quyền | Dữ liệu gửi | Response / nhiệm vụ |
| --- | --- | --- | --- | --- |
| POST | `/api/artist-applications` | Auth | JSON `artistName, bio, profileImage` đều không rỗng | `ArtistApplicationResponse`; gửi hồ sơ nghệ sĩ |
| GET | `/api/artist-applications/me` | Auth | — | `ArtistApplicationResponse[]`; lịch sử hồ sơ của tôi |
| GET | `/api/admin/artist-applications` | ADMIN | — | `ArtistApplicationResponse[]`; các đơn PENDING |
| GET | `/api/admin/artist-applications/{id}` | ADMIN | — | `ArtistApplicationResponse`; xem đơn |
| PATCH | `/api/admin/artist-applications/{id}/approve` | ADMIN | — | `ArtistApplicationResponse`; duyệt, cấp role ARTIST và tạo hồ sơ liên kết user |
| PATCH | `/api/admin/artist-applications/{id}/reject` | ADMIN | JSON `{rejectionReason}` không rỗng | `ArtistApplicationResponse`; từ chối và lưu lý do |

`profileImage` ở đây là URL chuỗi, không phải file. Trạng thái đơn: `PENDING`, `APPROVED`, `REJECTED`. Đang có đơn PENDING hoặc đã là ARTIST thì không gửi thêm; xử lý 409. Duyệt/từ chối chỉ áp dụng cho đơn PENDING.

## 9. Studio dành cho nghệ sĩ

Tất cả route dưới đây yêu cầu role **ARTIST** và hồ sơ nghệ sĩ liên kết với user. Backend tự xác định artistId từ token; sửa/xóa/submit chỉ được thao tác nội dung của mình.

| Method | Endpoint | Request | Response / nhiệm vụ |
| --- | --- | --- | --- |
| GET | `/api/artist/me` | — | `ArtistResponse`; hồ sơ nghệ sĩ đang đăng nhập |
| PUT | `/api/artist/me` | JSON `name, bio, profileImage` không rỗng | `ArtistResponse`; sửa hồ sơ, profileImage là URL |
| GET | `/api/artist/albums` | — | `ArtistContentResponse[]`; album của tôi, mọi trạng thái |
| POST | `/api/artist/albums` | FormData `name, image: File, description, bgColor` | `ArtistContentResponse`; tạo album DRAFT |
| PUT | `/api/artist/albums/{id}` | FormData `name, description, bgColor`; `image: File` tùy chọn | `ArtistContentResponse`; sửa album |
| DELETE | `/api/artist/albums/{id}` | — | `204`; xóa album DRAFT/REJECTED |
| PATCH | `/api/artist/albums/{id}/submit` | — | `ArtistContentResponse`; gửi duyệt album |
| GET | `/api/artist/songs` | — | `ArtistContentResponse[]`; bài của tôi, mọi trạng thái |
| GET | `/api/artist/songs/{id}` | — | `ArtistContentResponse`; chi tiết bài để sửa |
| POST | `/api/artist/songs` | FormData `name, file: File, image: File, lyrics, duration, albumId` | `ArtistContentResponse`; tạo bài DRAFT |
| PUT | `/api/artist/songs/{id}` | FormData `name, lyrics, duration, albumId`; `file, image` tùy chọn | `ArtistContentResponse`; sửa bài |
| DELETE | `/api/artist/songs/{id}` | — | `204`; xóa bài DRAFT/REJECTED |
| PATCH | `/api/artist/songs/{id}/submit` | — | `ArtistContentResponse`; gửi duyệt bài |

Các trường text ở create/update studio phải không rỗng. Album tạo mới cần ảnh dù DTO không đánh dấu NotNull: service kiểm tra file. Audio cần MIME `audio/*`; ảnh cần `image/*`. Album của bài hát phải thuộc cùng nghệ sĩ.

Luồng trạng thái: tạo `DRAFT` → submit `PENDING_REVIEW` → admin duyệt `PUBLISHED` hoặc từ chối `REJECTED`. Sửa REJECTED đưa về DRAFT và xóa lý do từ chối. Chỉ DRAFT/REJECTED được sửa, xóa hoặc submit. Admin chỉ disable nội dung PUBLISHED, chuyển sang `DISABLED`. Chưa có endpoint phục hồi DISABLED.

Duyệt album và bài hát là hai thao tác riêng; không tự suy ra duyệt album sẽ duyệt toàn bộ bài. UI nên hiển thị trạng thái của từng đối tượng.

## 10. Admin kiểm duyệt nội dung

Tất cả route dưới đây yêu cầu **ADMIN** và trả `ArtistContentResponse` hoặc array như ghi trong bảng.

| Method | Endpoint | Request | Nhiệm vụ / response |
| --- | --- | --- | --- |
| GET | `/api/admin/songs/pending` | — | Danh sách bài PENDING_REVIEW; array |
| PATCH | `/api/admin/songs/{id}/approve` | — | Duyệt bài đang chờ; object |
| PATCH | `/api/admin/songs/{id}/reject` | JSON `{rejectionReason}` không rỗng | Từ chối bài đang chờ; object |
| PATCH | `/api/admin/songs/{id}/disable` | — | Ẩn bài PUBLISHED; object |
| GET | `/api/admin/albums/pending` | — | Danh sách album PENDING_REVIEW; array |
| PATCH | `/api/admin/albums/{id}/approve` | — | Duyệt album đang chờ; object |
| PATCH | `/api/admin/albums/{id}/reject` | JSON `{rejectionReason}` không rỗng | Từ chối album đang chờ; object |
| PATCH | `/api/admin/albums/{id}/disable` | — | Ẩn album PUBLISHED; object |

## 11. Admin quản lý catalog và upload media

Tất cả route trong bảng này yêu cầu **ADMIN**. Đây là API quản trị catalog; studio của ARTIST dùng phần 9.

| Method | Endpoint | Request | Response / nhiệm vụ |
| --- | --- | --- | --- |
| POST | `/api/songs/create/` | FormData `name, file: File, image: File, lyrics, duration, playCount, albumId, artistId` | `SongResponse`; upload media và tạo bài trong DB; khi tạo mới gửi playCount=0 |
| PUT | `/api/songs/{id}/` | FormData `name, lyrics, duration, albumId, artistId`; `file, image` tùy chọn | `SongResponse`; cập nhật bài; cũng hỗ trợ route không `/` cuối |
| DELETE | `/api/songs/{id}/` | — | `204`; xóa bài |
| POST | `/api/songs/upload` | FormData `file: File, image: File` | `UploadSongResponse`; chỉ upload media, **không tạo bài trong DB** |
| POST | `/api/albums/create/` | JSON `name, image, description, bgColor` | `AlbumResponse`; tạo album, image là URL |
| PUT | `/api/albums/{id}/` | FormData `name, image: File, description, bgColor` | `AlbumResponse`; cập nhật album, ảnh là file thay mới |
| DELETE | `/api/albums/{id}/` | — | `200 rỗng`; xóa album |
| POST | `/api/artists/create/` | JSON `name, bio, profileImage` | `ArtistResponse`; tạo nghệ sĩ catalog, profileImage là URL |
| PUT | `/api/artists/{id}/` | FormData `name, bio, profileImage: File` | `ArtistResponse`; sửa nghệ sĩ, upload avatar |
| DELETE | `/api/artists/{id}/` | — | `200 rỗng`; xóa nghệ sĩ |

Không gọi `/songs/upload` rồi truyền fileUrl vào `/songs/create/`: create hiện nhận file multipart, không nhận URL audio. Với form tạo bài, gọi trực tiếp create để tránh upload hai lần. DTO tạo album admin chưa có artistId; DTO tạo nghệ sĩ admin chưa có userId, nên tạo catalog artist không đồng nghĩa cấp tài khoản ARTIST.

Form update song vẫn yêu cầu đầy đủ các trường text/ID bắt buộc, kể cả khi chỉ đổi ảnh. Ảnh album và ảnh bài hát là các trường riêng; thay bìa album không nên được frontend giả định tự đồng bộ tất cả ảnh bài hát.

## 12. Hợp đồng response để khai báo TypeScript

Các kiểu dưới đây giữ đúng tên trường backend. Timestamp là LocalDateTime dạng ISO, không có thông tin múi giờ; frontend không tự nối `Z` nếu chưa thống nhất múi giờ server. Các trường chưa có dữ liệu có thể null.

```ts
type Role = 'USER' | 'ARTIST' | 'ADMIN';
type ContentStatus = 'DRAFT' | 'PENDING_REVIEW' | 'PUBLISHED' | 'REJECTED' | 'DISABLED';

type RegisterResponse = { id: number; username: string; email: string };
type LoginResponse = RegisterResponse & { accessToken: string };
type AccessTokenResponse = { accessToken: string };
type UserProfileResponse = RegisterResponse & {
  active: boolean; role: Role; avatarUrl: string | null;
};
type SongResponse = {
  id: number; name: string; file: string; image: string; lyrics: string;
  duration: string; playCount: number; albumId: number; artistId: number;
  albumName: string; artistName: string;
};
type AlbumResponse = {
  id: number; name: string; image: string; description: string; bgColor: string;
};
type ArtistResponse = { id: number; name: string; bio: string; profileImage: string };
type PlaylistResponse = { id: number; name: string; image: string | null; userId: number };
type PageResponse<T> = {
  content: T[]; pageNumber: number; pageSize: number;
  totalElements: number; totalPages: number; last: boolean;
};
type SearchResponse = {
  songs: SongResponse[]; albums: AlbumResponse[]; artists: ArtistResponse[];
};
type StreamingResponse = {
  songId: number; songName: string; streamUrl: string; contentType: string;
};
type UploadSongResponse = { fileUrl: string; imageUrl: string };
type RecentlyPlayedResponse = {
  songId: number; songName: string; image: string; artistName: string; playedAt: string;
};
type ArtistApplicationResponse = {
  id: number; userId: number; artistName: string; bio: string; profileImage: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED'; submittedAt: string;
  reviewedAt: string | null; reviewedBy: number | null; rejectionReason: string | null;
};
type ArtistContentResponse = {
  id: number; name: string; artistId: number; albumId: number | null;
  status: ContentStatus; rejectionReason: string | null;
  createdAt: string; updatedAt: string; publishedAt: string | null;
  file: string | null; image: string; lyrics: string | null; duration: string | null;
};
type ErrorResponse = {
  timestamp: string; status: number; error: string; message: string; path: string;
  errors?: Record<string, string>;
};
```

`SongResponse.file` là URL phát, không phải `fileUrl`. Avatar user dùng `avatarUrl`, artist dùng `profileImage`, album/song/playlist dùng `image`. Map tên trường một lần trong lớp API của frontend.

`AlbumResponse` hiện không chứa artistId, status hoặc danh sách songs. `ArtistContentResponse` dùng chung cho song/album; với album thì albumId/file/lyrics/duration là null, và response không có description/bgColor dù form update cần hai trường này. UI sửa album studio cần giữ dữ liệu form hoặc bổ sung hợp đồng backend trước khi kỳ vọng tải lại đầy đủ dữ liệu draft. Chưa có `GET /api/artist/albums/{id}`.

## 13. Xử lý lỗi

| Status | Ý nghĩa | Hành vi frontend |
| --- | --- | --- |
| 400 | Sai query, JSON hoặc validation | Hiện lỗi trường từ `errors`; giữ nội dung form |
| 401 | Thiếu/sai token hoặc sai thông tin đăng nhập | Refresh một lần ở request phù hợp; thất bại thì yêu cầu đăng nhập |
| 403 | Thiếu role, không sở hữu tài nguyên hoặc thiếu hồ sơ nghệ sĩ | Hiện thông báo quyền; không refresh lặp |
| 404 | Không tồn tại hoặc không được công khai theo logic catalog | Hiện trang không tìm thấy, bỏ bài khỏi queue nếu cần |
| 409 | Trạng thái không cho phép hoặc dữ liệu xung đột | Hiện lý do, tải lại trạng thái đối tượng |
| 405 / 415 | Sai HTTP method / Content-Type | Kiểm tra cách gọi API |
| 500 | Lỗi server hoặc upload | Báo thất bại; không tự lặp POST tạo nội dung tránh bản trùng |

Phần lớn lỗi theo `ErrorResponse`; riêng catch RuntimeException trong `GET /api/users/me/` có thể trả 401 với body text. API client cần đọc text trước và parse JSON nếu hợp lệ, chấp nhận body rỗng. Trình duyệt có thể báo network/CORS error mà không có response status.

## 14. Ghép API vào các màn hình

| Màn hình | API cần dùng | Trách nhiệm frontend |
| --- | --- | --- |
| Trang chủ | top-songs, new-releases; personalized và recently-played khi login | Loading/empty/error riêng cho từng mục |
| Tìm kiếm | search | Debounce, tab songs/albums/artists, chống response cũ |
| Trang album | albums/{id}/ và albums/{id}/songs/ | Header, track list, play-all |
| Trang nghệ sĩ | artists/{id}/ và songs/filter?artistId=…; artist-follows | Avatar, bio, bài của nghệ sĩ, nút follow |
| Player và lyrics | songs/{id}/, stream, recently-played/play | Điều khiển audio, lyrics, queue, ghi một sự kiện/lượt nghe |
| Thư viện | favorites/me, artist-follows/me, playlists/ | Tách bài thích, nghệ sĩ theo dõi, playlist lọc theo userId |
| Chi tiết playlist | playlists/{id}, playlists/{id}/songs | Chỉ chủ sở hữu được thấy thao tác sửa/xóa/thêm/bỏ bài |
| Hồ sơ/cài đặt | users/me/, profile/, avatar/, change-password/ | Đồng bộ session sau cập nhật |
| Đăng ký nghệ sĩ | artist-applications và artist-applications/me | Form và lịch sử trạng thái |
| Artist Studio | artist/me, artist/albums, artist/songs và submit | Chặn nút sửa/xóa theo status, hiển thị rejectionReason |
| Admin review | admin/artist-applications, admin/songs/pending, admin/albums/pending | Duyệt/từ chối, làm mới danh sách sau thành công |
| Admin catalog | API quản trị ở phần 11 | FormData đúng trường, upload tiến trình và xử lý lỗi |

Chưa có API album theo artist, reorder playlist, playlist riêng tư, upload ZIP/lyrics hàng loạt, lyric timestamp, cập nhật duration tự động hoặc đồng bộ queue. Không dựng URL suy đoán cho các chức năng này. Với album của nghệ sĩ, có thể suy ra albumId từ các trang songs/filter đã tải, nhưng kết quả không đầy đủ nếu chưa tải hết và không bao gồm album chưa có bài.

## 15. Dữ liệu local tham khảo và cách đối chiếu

Theo lần import đã xác minh trong phiên trước: nghệ sĩ Phương Mỹ Chi ID `2`, album Dân Chơi Dân Ca ID `2`, 10 bài ID `12`–`21`. Đây là dữ liệu local tham khảo, không hardcode các ID vào chức năng sản phẩm và không coi là dữ liệu cố định sau khi seed/reset database.

```text
GET /api/albums/2/
GET /api/albums/2/songs/
GET /api/artists/2/
GET /api/songs/12/
GET /api/stream/song/12/play
```

Các đường dẫn hỗ trợ phát triển (GET Public theo security): `/actuator/health`, `/v3/api-docs`, `/swagger-ui/index.html`. Những actuator route khác bị chặn. OpenAPI giúp đối chiếu bản backend đang chạy; DTO/security/service trong source là căn cứ của bản tổng hợp này.

Nguồn đối chiếu trong repository: `presentation/controller/*Controller.java`, `application/dto/Request`, `application/dto/Response`, `infrastructure/config/SecurityConfig.java`, `CorsConfig.java`, `infrastructure/security/RefreshTokenCookieService.java`, các usecase/service và `src/main/resources/application.yml`. Khi đổi endpoint/DTO/quyền, cập nhật tài liệu này cùng thay đổi đó.
