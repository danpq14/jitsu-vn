@echo off
echo Building and starting Jitsu Logistics System...
echo.

echo Step 1: Building all services...
call gradlew build -x test

echo.
echo Step 2: Starting all services with Docker Compose...
docker-compose up -d --build

echo.
echo ✅ Jitsu system is starting up!
echo.
echo 🔄 Services will be available in ~60 seconds:
echo - API Gateway: http://localhost:8080
echo - All services: http://localhost:808[1-5]
echo.
echo 📊 Check status: docker-compose ps
echo 📝 View logs: docker-compose logs -f [service-name]
echo 🛑 Stop all: docker-compose down