# SM-Xplore (prototype)

Spring Boot 4.1.1 / Java 21 reactive prototype for the SM-Xplore tourism platform.
Hexagonal architecture (`domain` / `application` / `infrastructure`) with polyglot
persistence: PostgreSQL (R2DBC) + MongoDB (reactive).

## Run locally

Docker required (Testcontainers / compose).

```bash
docker compose up --build     # app + MongoDB + PostgreSQL (http://localhost:8080)
```

Or run only the databases and use the IDE:

```bash
docker compose up mongodb postgres -d
./gradlew bootRun             # expects Postgres on 5432 and Mongo on 27017
```

Build and tests:

```bash
./gradlew clean build         # Testcontainers starts Mongo + Postgres automatically
```

## Security

All endpoints require a **JWT Bearer token** except `/api/v1/auth/register`,
`/api/v1/auth/login`, `/api/v1/auth/recover-password`, `/actuator/health` and the
Swagger UI / docs paths.

1. **Register** (`POST /api/v1/auth/register`): public self-registration accepts only
   `TOURIST` and `SERVICE_PROVIDER` (the latter stays `INACTIVE/PENDING` until
   approved). Other roles must be assigned by an administrator
   (`PATCH /api/v1/users/{id}/role`).
2. **First administrator**: set `ADMIN_EMAIL` and `ADMIN_PASSWORD` (optional
   `ADMIN_DNI_NUMBER` / `ADMIN_DNI_TYPE`); the app creates the account at startup
   when it does not exist (idempotent). The docker compose dev defaults are
   `admin@ahorasi.local` / `Admin123*`.
3. **Login** returns `{ token, tokenType, expiresIn, user }`. Send it as
   `Authorization: Bearer <token>`; in Swagger use the **Authorize** button.
4. **Roles**: `ADMIN`, `DESTINATION_MANAGER`, `GUIDE`, `SERVICE_PROVIDER`, `TOURIST`.
   The endpoint-to-role matrix lives in a single place: `SecurityConfiguration`.
5. **CORS**: allowed origins come from `CORS_ALLOWED_ORIGINS` (default
   `http://localhost:*` for development). Set the real frontend origin in any
   deployment.
6. **Secrets**: `JWT_SECRET` must be at least 32 bytes (the app fails fast at
   startup otherwise). The committed default is for local development only.

### Security notes / known gaps

- The chain enforces **roles, not resource ownership**, except for the profile
  endpoints (`PUT`/`DELETE /api/v1/users/{id}` are self-or-admin). Cross-user
  ownership checks for reservations, services and security reports are pending —
  tracked as follow-ups with each module owner.
- No refresh tokens and no revocation: a token stays valid until `exp`
  (60 minutes by default, `JWT_EXPIRATION_MINUTES`); blocking a user does not
  invalidate tokens already issued.
- 401/403 responses share the same `ApiErrorResponse` shape as the rest of the API.
