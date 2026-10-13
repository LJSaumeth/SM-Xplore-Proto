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

All endpoints require a **JWT Bearer token** except the public surface:
`/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/recover-password`,
`/actuator/health`, the Swagger UI / docs paths, the **public catalog**
(`GET` of `/api/v1/events`, `/api/v1/activities`, `/api/v1/services`,
`/api/v1/zones` and `/api/v1/sites`) and the **public profiles**
(`GET /api/v1/users/guides/{id}` and `/api/v1/users/providers/{id}` — contact
data is only included for authenticated callers).

1. **Register** (`POST /api/v1/auth/register`): public self-registration accepts
   `TOURIST`, `SERVICE_PROVIDER`, `GUIDE` and `DESTINATION_MANAGER`; the latter
   three stay `INACTIVE` until an administrator approves them
   (`POST /api/v1/users/{id}/approve-provider`, `/approve-guide`, `/approve-manager`;
   a pending provider can also be rejected with `/reject-provider`).
   Pending registrations are listed with
   `GET /api/v1/users/search?role=<ROLE>&status=INACTIVE`. Changing a user's role
   is not supported (each role has its own profile data): accounts must be created
   with the role they need. Only `ADMIN` cannot self-register (bootstrap only).
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

- The chain enforces **roles**; **resource ownership** is enforced in the
  controllers: `GET`/`PUT`/`DELETE /api/v1/users/{id}` are owner-or-admin;
  reservations (including their items) are owner/provider/guide/staff scoped —
  creation and cancellation belong to the owner, `confirm`/`reject`/`attendance`/
  `no-show` to the reservation's provider or guide; activities belong to their
  guide, services to their provider, report text/photos to the report's author
  (staff can do everything), and the reporter/commenter always comes from the
  token (never the body).
- Public profiles (`GET /api/v1/users/guides/{id}`, `/users/providers/{id}`) only
  expose active (approved) accounts; email/phone are returned to authenticated
  callers only (anonymous requests get them as `null`).
- Role changes are rejected (400): each role has its own profile data, so an
  account must be created with the role it needs (see the `approve-*` flows).
- Provider registrations can be rejected by admins
  (`POST /api/v1/users/{id}/reject-provider`); guide/destination-manager reject
  flows are not exposed yet. Blocked or suspended accounts can be reactivated by an
  admin with `PATCH /api/v1/users/{id}/status` (`ACTIVE`). Pending (`INACTIVE`)
  accounts cannot be activated directly — they must go through the `approve-*`
  endpoints, and a provider can only log in once its registration is `APPROVED`.
- **Sites & zones** (zones in PostgreSQL, sites in MongoDB — public reads,
  `DESTINATION_MANAGER`/`ADMIN` management), **catalog filters** by site/zone,
  **event editing** (`PUT /events/{id}`), **reservations by provider/guide/item**
  plus a readable code (`R-####`), and **solved reports**
  (`POST /security-reports/{id}/solve`).
- No refresh tokens and no revocation: a token stays valid until `exp`
  (60 minutes by default, `JWT_EXPIRATION_MINUTES`); blocking a user does not
  invalidate tokens already issued.
- 401/403 responses share the same `ApiErrorResponse` shape as the rest of the API.
