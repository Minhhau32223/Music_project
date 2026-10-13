# Music App Backend

Backend REST API cho ứng dụng nghe nhạc, xây dựng bằng Spring Boot và Clean Architecture. Hệ thống cung cấp xác thực JWT, quản lý bài hát/album/nghệ sĩ, playlist, yêu thích, follow, lịch sử nghe, tìm kiếm, đề xuất và streaming MP3 qua Cloudinary.

## Tính năng

- Đăng ký, đăng nhập, refresh/logout token và phân quyền `USER`, `ARTIST`, `ADMIN`.
- CRUD bài hát, album, nghệ sĩ và hồ sơ người dùng.
- Upload MP3/ảnh/lyrics lên Cloudinary; thời lượng audio được backend đọc tự động từ metadata.
- Playlist, yêu thích, follow nghệ sĩ và recently played.
- Tìm kiếm, top charts, new releases và personalized recommendations.
- Artist application, artist studio và quy trình moderation nội dung.
- PostgreSQL với Flyway migration, Redis tùy chọn, CORS và xử lý lỗi thống nhất.

## Kiến trúc

Dự án áp dụng Clean Architecture, tách rõ các lớp:

```text
src/main/java/com/spotify/spotify_backend/
├── domain/          # Entity, value object, repository contract
├── application/     # DTO, mapper, use case và business rules
├── infrastructure/  # JPA, PostgreSQL, Cloudinary, Redis, migration
└── presentation/    # REST controller, security, exception handler
```

Controller chỉ phụ thuộc vào use case trong `application`; chi tiết JPA/Cloudinary nằm ở `infrastructure`. Cấu trúc này giúp kiểm thử business logic và thay thế adapter mà không đổi domain.

## Công nghệ

| Thành phần | Công nghệ |
|---|---|
| Runtime | Java 17+, Maven Wrapper |
| Backend | Spring Boot, Spring Web, Spring Security |
| Database | PostgreSQL, Spring Data JPA, Flyway |
| Authentication | JWT access/refresh token, HttpOnly cookie |
| Media | Cloudinary (MP3, cover image) |
| Cache | Redis tùy chọn |
| Tests | JUnit, Spring Boot integration tests |
| Local runtime | Docker Compose |

## Khởi chạy nhanh

### Yêu cầu

- Java 17 hoặc mới hơn
- Docker Desktop và Docker Compose
- Tài khoản Cloudinary nếu cần upload media

### Cấu hình

```powershell
Copy-Item .env.example .env
```

Điền các giá trị trong `.env`, tối thiểu là `DB_PASSWORD`, `JWT_SECRET` và thông tin Cloudinary khi dùng upload. Không commit `.env` hoặc credential thật.

### Chạy toàn bộ service bằng Docker

```powershell
docker compose up -d --build
```

API mặc định chạy tại `http://localhost:8000`. PostgreSQL được expose theo `POSTGRES_PORT` (mặc định `5432`) để kết nối bằng pgAdmin; Redis chỉ cần khi bật cache.

### Chạy backend bằng Maven

```powershell
./mvnw spring-boot:run
```

Chạy test:

```powershell
./mvnw test
```

## API

Base URL: `http://localhost:8000/api`

Các nhóm API chính:

- `/auth` — đăng ký, login, refresh, logout.
- `/users` — tài khoản và profile hiện tại.
- `/songs`, `/albums`, `/artists` — catalog và CRUD nội dung.
- `/search`, `/recommendations` — tìm kiếm và đề xuất.
- `/stream` — phát audio và ghi nhận lượt nghe.
- `/playlists`, `/favorites`, `/artist-follows`, `/recently-played` — tính năng cá nhân.
- `/artist-applications`, `/artist` — artist onboarding và studio.
- `/admin` — duyệt đơn và moderation nội dung.

Template request/response đầy đủ, quyền truy cập, multipart fields, mã lỗi và ví dụ cURL được duy trì trong tài liệu phát triển local. Frontend không truy cập trực tiếp PostgreSQL; mọi dữ liệu đi qua REST API.

Khi upload hoặc thay file MP3, chỉ gửi `name`, `file`, `image`, `lyrics`, `albumId` nếu có. Không gửi `duration`: backend lấy thời lượng thật từ metadata audio và trả lại trong response.

## Cấu hình môi trường

Repository chỉ công khai **tên biến** và giá trị mẫu rỗng. Không đưa giá trị thật của các biến dưới đây vào README, `.env.example`, log, issue hoặc Git:

| Nhóm | Biến | Mức độ nhạy cảm | Mục đích |
|---|---|---|---|
| Database | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | `DB_PASSWORD` là secret | Kết nối PostgreSQL |
| JWT | `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION` | `JWT_SECRET` là secret nghiêm trọng | Ký và hết hạn token |
| Cloudinary | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | `CLOUDINARY_API_SECRET` là secret | Lưu trữ media |
| CORS | `CORS_ALLOWED_ORIGINS` | Cấu hình bảo mật | Whitelist frontend |
| Cache | `CACHE_TYPE`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` | `REDIS_PASSWORD` là secret nếu bật | Cấu hình Redis |
| Runtime | `POSTGRES_PORT`, `PORT`, `API_DOCS_ENABLED` | Không phải secret | Port và tính năng runtime |
| Seed local | `APP_SEED_ENABLED`, `DEV_SEED_PASSWORD` | `DEV_SEED_PASSWORD` là secret local | Dữ liệu phát triển |

### Quy tắc bảo mật cấu hình

- `.env` chỉ tồn tại local hoặc trong secret manager của môi trường triển khai; file này đã được ignore.
- `.env.example` chỉ giữ tên biến và placeholder, không dùng password, token, API key hoặc secret thật.
- Tạo `JWT_SECRET` ngẫu nhiên đủ dài cho từng môi trường; không dùng lại secret development ở production.
- Giới hạn `CORS_ALLOWED_ORIGINS` vào đúng domain frontend cần thiết, không dùng `*` khi có xác thực.
- Rotate ngay database password, JWT secret, Redis password hoặc Cloudinary secret nếu từng bị commit, log hoặc chia sẻ ngoài hệ thống.
- Tắt `APP_SEED_ENABLED` và `API_DOCS_ENABLED` trong production nếu không có yêu cầu vận hành rõ ràng.

Dùng [`.env.example`](.env.example) làm danh sách biến cần cấu hình, sau đó tạo `.env` local hoặc khai báo chúng trong secret manager của Docker/CI/CD. Không copy giá trị bí mật vào tài liệu công khai.

## Seed dữ liệu demo

Seed tài khoản và catalog demo được bật có chủ đích bằng `APP_SEED_ENABLED=true`. Mật khẩu admin lấy từ `SEED_ADMIN_PASSWORD`, được hash bằng BCrypt trước khi lưu; giá trị mật khẩu không nằm trong source code hay migration. Các file MP3/ảnh được mount ngoài container qua `MEDIA_SEED_DIR` và `MEDIA_SEED_IMAGE_DIR`, sau đó upload lên Cloudinary khi `MEDIA_SEED_ENABLED=true`.

Flyway chỉ quản lý schema và migration cấu trúc. Việc đưa binary media lên storage ngoài và hash mật khẩu bằng secret runtime không được đặt trong SQL migration, vì migration không nên chứa file media hoặc credential cố định. Khi deploy, mount thư mục media đã được cấp quyền sử dụng, khai báo secret trong secret manager, bật hai cờ seed, rồi khởi động backend; runner sẽ import idempotent theo tên bài hát.

## Repository hygiene

- `.env`, credential, media local, database dump, build output, test report và log không được commit.
- Tài liệu Markdown chỉ giữ `README.md` ở root; tài liệu phát triển/đặc tả đặt ngoài Git hoặc được ignore theo `.gitignore`.
- Dữ liệu media được lưu ở Cloudinary, không lưu file MP3 trong repository.

## License

Dự án phục vụ mục đích học tập và phát triển cá nhân. Chỉ sử dụng nội dung âm thanh/hình ảnh mà bạn có quyền sử dụng.
