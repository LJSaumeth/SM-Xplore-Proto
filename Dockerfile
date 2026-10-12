# syntax=docker/dockerfile:1

# ---------- etapa de build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Primero el wrapper y los scripts de Gradle: esta capa se cachea mientras
# no cambien las dependencias. El cache de Gradle (/root/.gradle) se conserva
# entre builds para no volver a descargar todo.
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon --console=plain

# Luego el código y el empaquetado del jar ejecutable
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar --no-daemon --console=plain

# ---------- etapa de runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --create-home appuser

COPY --from=build /workspace/build/libs/*.jar app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
