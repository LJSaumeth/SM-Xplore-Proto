# AGENTS.md

Spring Boot **4.1.1** / Java **21** prototype for SM-Xplore (tourism platform). Gradle project, single module (`rootProject.name = "proto"`). Early-stage: only the application entrypoint exists.

## Commands

Use the Gradle wrapper; do not rely on a system Gradle install.

- Build: `.\gradlew.bat build`
- Test: `.\gradlew.bat test`
- Run app: `.\gradlew.bat bootRun`
- Single test: `.\gradlew.bat test --tests "com.smxplore.proto.AhorasiApplicationTests"`
- Devtools live reload is on; `/actuator` and Prometheus metrics are exposed via actuator.

Tests use **Testcontainers** (`TestcontainersConfiguration`) for MongoDB + PostgreSQL, so **Docker must be running**. `TestAhorasiApplication` starts the app against those containers for manual testing.

## Architecture (see `src/docs/architecture-guidelines.md`, authoritative)

- Hexagonal / Ports & Adapters with exactly three layers: `domain`, `application`, `infrastructure`.
- All code (classes, methods, variables, packages, messages) must be in **English**.
- `domain/`: `exceptions/` (extend `RuntimeException`, descriptive names), `model/<entity>/` (subfolder per entity, validations + behavior methods live here), `ports/repository/`, `ports/usecases/`.
- Entity relations use **UUID IDs**, never object references.
- Use cases: one method named `handle`; name ports `INombreUseCase` or `NombreUseCasePort`.
- `application` / `infrastructure` layers are not yet fleshed out.

## Naming gotchas (legacy, do not "fix" casually)

- Package is `com.smxplore.proto`, but Gradle `group` is `com.sm-xplore` and `description` is "prototype of SM-Xplore".
- Main class and DB name still use the old codename `Ahorasi` (`AhorasiApplication`, `spring.application.name: ahorasi`). Verify with the team before renaming.

## Stack notes

- Java 21 toolchain is pinned; `settings.gradle.kts` uses `foojay-resolver-convention` 1.0.0 (Gradle 9 requires 1.0.0) so Gradle auto-downloads JDK 21. A local JDK 21 is not required, but the first build needs network access.
- Lombok + MapStruct are both annotation processors; keep their `annotationProcessor` entries in `build.gradle.kts`.
- Persistence: Spring Data JPA (PostgreSQL) and Spring Data MongoDB together. `application.yaml` currently has no datasource config (supplied by Testcontainers in tests).
- OpenAPI UI via springdoc (`springdoc-openapi-starter-webmvc-ui:3.1.0`).

## Docs

`src/docs/` (Spanish) holds requirements, user stories, UML/PlantUML models, and the architecture guide. `src/docs/` is currently untracked on branch `users`.
