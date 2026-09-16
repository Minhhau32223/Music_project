# NOCTURNE authentication contract

Base path: `/api`. The frontend keeps the access token in memory. The refresh token is a host-only, HttpOnly cookie named `nocturne_refresh`, scoped to `/api/auth`.

| Method | Endpoint | Auth | Request | Success | Failure | Cookie |
|---|---|---|---|---|---|---|
| POST | `/auth/register/` | Public | `{ username, email, password }` | `200 { id, username, email }` | `400`, `409` | None |
| POST | `/auth/login/` | Public | `{ email, password }` | `200 { id, username, email, accessToken }` | `400`, `401` | Sets refresh cookie |
| POST | `/auth/refresh/` | Refresh cookie | None | `200 { accessToken }` | `401` | Reads refresh cookie |
| POST | `/auth/logout/` | Public | None | `204` | — | Expires refresh cookie |
| GET | `/users/me/` | Bearer access token | None | `200 { id, username, email, active, role, avatarUrl }` | `401` | None |

Password length is 6–100 characters. Register creates the account only; it does not create a session.

Local cookie defaults: `HttpOnly`, `Secure=false`, `SameSite=Lax`, host-only, path `/api/auth`. Production must use HTTPS with `AUTH_COOKIE_SECURE=true`; use `AUTH_COOKIE_SAME_SITE=None` only when frontend and API are truly cross-site.
