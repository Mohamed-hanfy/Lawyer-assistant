# Multi-stage build for Spring Boot application with Google Drive OAuth2 support

# Build stage
FROM maven:3.9-eclipse-temurin-24-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Install required packages for OAuth2 flow (if needed for debugging)
RUN apk add --no-cache curl

# Create directories for Google Drive credentials and tokens
RUN mkdir -p /app/credentials /app/tokens

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy Google credentials (client_secret.json) - mount as volume in production
# IMPORTANT: These files should be mounted as volumes for security
# Example: -v ./credentials:/app/credentials:ro
COPY src/main/java/com/mohamed/lawyer/storage/client_secret.json /app/credentials/client_secret.json

# Expose application port
EXPOSE 8080

# Set environment variables for Google Drive paths
ENV GOOGLE_CREDENTIALS_PATH=/app/credentials/client_secret.json
ENV GOOGLE_TOKENS_PATH=/app/tokens

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point with JVM optimizations
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]

# To run with OAuth2 token generation:
# 1. First time setup (requires interactive browser):
#    docker run -it -p 8080:8080 -p 8888:8888 \
#      -v ./tokens:/app/tokens \
#      -v ./credentials:/app/credentials:ro \
#      lawyer-app
#
# 2. Subsequent runs (tokens already exist):
#    docker run -d -p 8080:8080 \
#      -v ./tokens:/app/tokens \
#      -v ./credentials:/app/credentials:ro \
#      lawyer-app
#
# IMPORTANT NOTES:
# - Mount tokens directory as volume to persist OAuth2 tokens between container restarts
# - Mount credentials directory with client_secret.json (read-only recommended)
# - For first-time OAuth2 authorization, you may need to:
#   1. Run container with port 8080-8090 exposed (for OAuth callback)
#   2. Access the authorization URL from logs
#   3. Complete authorization in browser
#   4. Tokens will be saved in mounted /app/tokens directory
# - Alternative: Pre-authorize and copy tokens to ./tokens/ directory before running
