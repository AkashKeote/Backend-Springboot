#!/bin/sh

# Startup script for EcoBazaarX Backend on Render

echo "🚀 Starting EcoBazaarX Backend..."
echo "📍 Environment: ${SPRING_PROFILES_ACTIVE:-production}"
echo "🌐 Port: ${PORT:-10000}"
echo "🗄️ Database: ${DATABASE_URL:0:50}..."

# Start the Spring Boot application
exec java -Xmx512m \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
    -Dserver.port=${PORT:-10000} \
    -Dserver.address=0.0.0.0 \
    -jar app.jar