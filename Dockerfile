# Multi-stage build for all Jitsu services
FROM openjdk:17-jdk as builder

WORKDIR /app

# Copy gradle files
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Copy source code for all modules
COPY common ./common
COPY booking-service ./booking-service
COPY ticket-service ./ticket-service
COPY assignment-service ./assignment-service
COPY event-processor ./event-processor
COPY api-gateway ./api-gateway

# Build all services
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# Runtime stage - we'll use this as a base for individual service images
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Default entrypoint (will be overridden by individual services)
ENTRYPOINT ["java", "-jar", "app.jar"]