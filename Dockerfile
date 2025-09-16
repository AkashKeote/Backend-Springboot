# Use Eclipse Temurin (more reliable than openjdk)
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage - using JRE for smaller image
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy the jar and startup script from builder stage
COPY --from=builder /app/target/ecobazaar-backend-1.0.0.jar app.jar
COPY start.sh .

# Make startup script executable
RUN chmod +x start.sh

# Create non-root user for security
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring
RUN chown spring:spring app.jar start.sh
USER spring

# Expose port
EXPOSE 10000

# Set default environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application using startup script
CMD ["./start.sh"]