# Build context should be the project root directory
# docker build -f server/Dockerfile .

# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /project

# Copy Gradle wrapper and config for dependency caching
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/libs.versions.toml gradle/libs.versions.toml

# Copy server module source
COPY server/build.gradle.kts server/
COPY server/src/ server/src/

# Build the fat JAR
RUN chmod +x gradlew && ./gradlew :server:buildFatJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

RUN groupadd -r skintrack && useradd -r -g skintrack -m skintrack

WORKDIR /app

# Copy the fat JAR from build stage
COPY --from=build /project/server/build/libs/*-all.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R skintrack:skintrack /app

USER skintrack

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xmx512m -Xms256m -XX:+UseStringDeduplication"

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
