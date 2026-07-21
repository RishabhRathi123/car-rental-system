# syntax=docker/dockerfile:1

# ---------- Stage 1: build the Spring Boot jar ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first (only re-downloads when pom.xml changes)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

# Copy sources and build. Tests are skipped here because they need a live DB;
# run them locally with `mvn test` against your own MySQL.
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true clean package

# ---------- Stage 2: lightweight runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar (there is exactly one *.jar in target after packaging)
COPY --from=build /app/target/*.jar app.jar

# Spring reads server.port from the PORT env var (defaults to 8080)
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
