# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Copy source code
COPY src/ src/

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean install -DskipTests

# Expose port
EXPOSE 10000

# Set default environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=10000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:10000/healthz || exit 1

# Run the application
CMD ["java", "-jar", "target/ecobazaar-backend-1.0.0.jar"]