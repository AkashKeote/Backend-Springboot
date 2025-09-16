# Multi-stage build for faster deployment
FROM openjdk:17-jdk-slim as builder

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

# Runtime stage
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/ecobazaar-backend-1.0.0.jar app.jar

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown spring:spring app.jar
USER spring

# Expose port (Render will override with PORT env var)
EXPOSE 10000

# Set default environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=10000

# Run the application with optimized JVM settings
CMD ["java", "-Xmx512m", "-Dserver.port=${PORT:-10000}", "-jar", "app.jar"]