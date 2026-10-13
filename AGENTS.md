# AGENTS.md

Spring Boot **4.1.1** / Java **21** prototype for SM-Xplore (tourism platform). Gradle project, single module (`rootProject.name = "proto"`). Several modules are implemented: users + auth/JWT, sites, events/activities, services, security reports and reservations.

## Commands

Use the Gradle wrapper; do not rely on a system Gradle install.

- Build: `.\gradlew.bat build`
- Test: `.\gradlew.bat test`
- Run app: `.\gradlew.bat bootRun`
- Single test: `.\gradlew.bat test --tests "com.smxplore.proto.AhorasiApplicationTests"`
- Devtools live reload is on; actuator exposes `/actuator/health` by default (a Prometheus registry is configured).

Tests use **Testcontainers** (`TestcontainersConfiguration`) for MongoDB + PostgreSQL, so **Docker must be running**. `TestAhorasiApplication` starts the app against those containers for manual testing.

## Architecture (see `src/docs/architecture-guidelines.md`, authoritative)

- Hexagonal / Ports & Adapters with exactly three layers: `domain`, `application`, `infrastructure`.
- All code (classes, methods, variables, packages, messages) must be in **English**.
- `domain/`: `exceptions/` (extend `RuntimeException`, descriptive names), `model/<entity>/` (subfolder per entity, validations + behavior methods live here), `ports/repository/`, `ports/usecases/`.
- Entity relations use **UUID IDs**, never object references.
- Use cases: one method named `handle`; name ports `INombreUseCase` or `NombreUseCasePort`.
- `application` / `infrastructure`: implemented per module — use cases in `application`, web controllers/DTOs (MapStruct), persistence adapters (Postgres via R2DBC / Mongo reactive) and configuration classes in `infrastructure`.

## Naming gotchas (legacy, do not "fix" casually)

- Package is `com.smxplore.proto`, but Gradle `group` is `com.sm-xplore` and `description` is "prototype of SM-Xplore".
- Main class and DB name still use the old codename `Ahorasi` (`AhorasiApplication`, `spring.application.name: ahorasi`). Verify with the team before renaming.

## Stack notes

- Java 21 toolchain is pinned; `settings.gradle.kts` uses `foojay-resolver-convention` 1.0.0 (Gradle 9 requires 1.0.0) so Gradle auto-downloads JDK 21. A local JDK 21 is not required, but the first build needs network access.
- Lombok + MapStruct are both annotation processors; keep their `annotationProcessor` entries in `build.gradle.kts`.
- Persistence: Spring Data **R2DBC** (PostgreSQL, Users context; schema in `src/main/resources/schema.sql`, run via `spring.sql.init`) and Spring Data **MongoDB Reactive** (services / security reports). `Users` uses joined-table inheritance (`users` + one table per role).
- OpenAPI UI via springdoc (`springdoc-openapi-starter-webflux-ui:3.1.0`).
- Security: stateless **JWT** (HS256, `app.security.jwt.*`; `JWT_SECRET` must be ≥ 32 bytes) with role-based authorization in `SecurityConfiguration` (single matrix) and **resource ownership checks in the controllers** (users, reservations, activities, services, reports). Public: auth endpoints, health, Swagger and the read-only catalog (`GET` of events/activities/services/zones/sites plus public guide/provider profiles — contact data only for authenticated callers). The first admin is bootstrapped from `ADMIN_EMAIL`/`ADMIN_PASSWORD`; guide, service-provider and destination-manager registrations are approved by admins via `POST /api/v1/users/{id}/approve-provider|approve-guide|approve-manager` (providers can be rejected via `reject-provider`). See the README "Security" section for the full flow and known gaps.

## Docs

`src/docs/` (Spanish) holds requirements, user stories, UML/PlantUML models, and the architecture guide. `src/docs/` is currently untracked on branch `users`.
