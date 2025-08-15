# Jitsu Logistics System

A comprehensive fast-mile logistics platform built with microservices architecture using Spring Boot, Java 17, and Gradle.

## Quick Start

### Prerequisites
- Java 17
- Docker & Docker Compose
- Gradle (wrapper included)

### Setup & Run

#### 🚀 Super Simple Startup (One Command!)
```bash
# Windows
start.bat

# Linux/Mac  
./start.sh
```

That's it! This will:
- Build all services
- Start PostgreSQL + Kafka + all microservices via Docker  
- Auto-create database schema + sample data
- Everything runs in containers

#### Useful Commands
```bash
docker-compose ps          # Check service status
docker-compose logs -f     # View all logs  
docker-compose down        # Stop everything
```

## Services & Endpoints

- **API Gateway:** http://localhost:8080
- **Booking Service:** http://localhost:8082
- **Ticket Service:** http://localhost:8083
- **Assignment Service:** http://localhost:8084
- **Event Processor:** http://localhost:8085

## Default Users

- **Admin:** username: `admin`, password: `admin123`
- **Drivers:** username: `driver1/driver2/driver3/driver4`, password: `driver123`

## API Documentation

- Swagger UI available at each service's `/swagger-ui.html` endpoint.

## Architecture

- **Microservices:** 5 independent services
- **Database:** Shared PostgreSQL database
- **Messaging:** Kafka for event processing
- **Gateway:** Spring Cloud Gateway for routing
- **Security:** Spring Security with role-based access

## Workflow

1. Admin creates booking sessions with time windows and target drivers
2. Admin adds tickets to sessions with zone assignments  
3. Drivers book tickets during booking windows
4. **Drivers claim assignments matching their ticket zones**
5. **Admin can override driver claim/unclaim actions**
6. All operations are logged via Kafka events

## APIs

See `API-ENDPOINTS.md` for complete API reference.

## Testing

- See `HOW-TO-TEST.md` for detailed testing instructions
- Import Postman collection `Test Flow.postman_collection.json` to your Postman for API testing