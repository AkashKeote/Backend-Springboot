# EcoBazaarX Backend Deployment Script (PowerShell)
# This script helps deploy the backend to different platforms

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("build", "start", "stop", "logs", "test", "clean", "restart")]
    [string]$Action
)

Write-Host "🚀 EcoBazaarX Backend Deployment Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Check if Docker is installed
try {
    docker --version | Out-Null
} catch {
    Write-Host "❌ Docker is not installed. Please install Docker first." -ForegroundColor Red
    exit 1
}

# Function to build Docker image
function Build-Image {
    Write-Host "🔨 Building Docker image..." -ForegroundColor Yellow
    docker build -t ecobazaar-backend:latest .
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Docker image built successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to build Docker image" -ForegroundColor Red
        exit 1
    }
}

# Function to run with docker-compose
function Start-Services {
    Write-Host "🐳 Starting services with Docker Compose..." -ForegroundColor Yellow
    docker-compose up -d
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Services started successfully!" -ForegroundColor Green
        Write-Host "🌐 Backend is running at: http://localhost:10000" -ForegroundColor Cyan
        Write-Host "🗄️ MySQL is running at: localhost:3307" -ForegroundColor Cyan
        Write-Host "📊 Health check: http://localhost:10000/healthz" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Failed to start services" -ForegroundColor Red
        exit 1
    }
}

# Function to stop services
function Stop-Services {
    Write-Host "🛑 Stopping services..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✅ Services stopped!" -ForegroundColor Green
}

# Function to show logs
function Show-Logs {
    Write-Host "📋 Showing logs..." -ForegroundColor Yellow
    docker-compose logs -f
}

# Function to run tests
function Run-Tests {
    Write-Host "🧪 Running tests..." -ForegroundColor Yellow
    ./mvnw test
}

# Function to clean up
function Clean-Up {
    Write-Host "🧹 Cleaning up..." -ForegroundColor Yellow
    docker-compose down -v
    docker image prune -f
    Write-Host "✅ Cleanup completed!" -ForegroundColor Green
}

# Function to restart services
function Restart-Services {
    Stop-Services
    Start-Sleep -Seconds 2
    Start-Services
}

# Execute based on action parameter
switch ($Action) {
    "build" { Build-Image }
    "start" { Start-Services }
    "stop" { Stop-Services }
    "logs" { Show-Logs }
    "test" { Run-Tests }
    "clean" { Clean-Up }
    "restart" { Restart-Services }
}

Write-Host "🎉 Operation completed!" -ForegroundColor Green