#!/bin/bash

# EcoBazaarX Backend Deployment Script
# This script helps deploy the backend to different platforms

echo "🚀 EcoBazaarX Backend Deployment Script"
echo "========================================"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Function to build Docker image
build_image() {
    echo "🔨 Building Docker image..."
    docker build -t ecobazaar-backend:latest .
    if [ $? -eq 0 ]; then
        echo "✅ Docker image built successfully!"
    else
        echo "❌ Failed to build Docker image"
        exit 1
    fi
}

# Function to run with docker-compose
run_compose() {
    echo "🐳 Starting services with Docker Compose..."
    docker-compose up -d
    if [ $? -eq 0 ]; then
        echo "✅ Services started successfully!"
        echo "🌐 Backend is running at: http://localhost:10000"
        echo "🗄️ MySQL is running at: localhost:3307"
        echo "📊 Health check: http://localhost:10000/healthz"
    else
        echo "❌ Failed to start services"
        exit 1
    fi
}

# Function to stop services
stop_services() {
    echo "🛑 Stopping services..."
    docker-compose down
    echo "✅ Services stopped!"
}

# Function to show logs
show_logs() {
    echo "📋 Showing logs..."
    docker-compose logs -f
}

# Function to run tests
run_tests() {
    echo "🧪 Running tests..."
    ./mvnw test
}

# Function to clean up
cleanup() {
    echo "🧹 Cleaning up..."
    docker-compose down -v
    docker image prune -f
    echo "✅ Cleanup completed!"
}

# Main menu
case "$1" in
    "build")
        build_image
        ;;
    "start")
        run_compose
        ;;
    "stop")
        stop_services
        ;;
    "logs")
        show_logs
        ;;
    "test")
        run_tests
        ;;
    "clean")
        cleanup
        ;;
    "restart")
        stop_services
        sleep 2
        run_compose
        ;;
    *)
        echo "Usage: $0 {build|start|stop|logs|test|clean|restart}"
        echo ""
        echo "Commands:"
        echo "  build   - Build Docker image"
        echo "  start   - Start services with docker-compose"
        echo "  stop    - Stop all services"
        echo "  logs    - Show service logs"
        echo "  test    - Run unit tests"
        echo "  clean   - Clean up containers and images"
        echo "  restart - Restart all services"
        exit 1
        ;;
esac