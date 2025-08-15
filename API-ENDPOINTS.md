# Jitsu API Endpoints Reference

## Base URLs
- **API Gateway:** http://localhost:8080 (routes to all services)
- **Auth Service:** http://localhost:8081  
- **Booking Service:** http://localhost:8082
- **Ticket Service:** http://localhost:8083
- **Assignment Service:** http://localhost:8084

## Authentication
- **Method:** Basic HTTP Authentication
- **Admin:** `admin` / `admin123`
- **Drivers:** `driver1-4` / `driver123`

## Core API Endpoints

### 🔐 Auth Service (Port 8081)
```http
POST /api/auth/login                    # Login user
GET  /api/auth/users                    # Get all users (Admin)  
GET  /api/auth/users/{username}         # Get user by username
```

### 📅 Booking Service (Port 8082)
```http
GET  /api/booking-sessions              # Get all booking sessions
GET  /api/booking-sessions/{id}         # Get booking session by ID
GET  /api/booking-sessions/region/{code} # Get sessions by region
GET  /api/booking-sessions/driver/{id}  # Get sessions for driver
POST /api/booking-sessions              # Create session (Admin)
PUT  /api/booking-sessions/{id}         # Update session (Admin)
DELETE /api/booking-sessions/{id}       # Delete session (Admin)
```

### 🎫 Ticket Service (Port 8083)
```http
GET  /api/tickets                       # Get all tickets
GET  /api/tickets/{id}                  # Get ticket by ID
GET  /api/tickets/booking-session/{id}  # Get tickets by session
GET  /api/tickets/booking-session/{id}/available # Available tickets
GET  /api/tickets/driver/{id}           # Get driver's tickets
POST /api/tickets                       # Create ticket (Admin)
POST /api/tickets/{id}/book             # Book ticket (Driver)
POST /api/tickets/{id}/unbook           # Unbook ticket (Driver)
POST /api/tickets/{id}/admin-book       # Admin book for driver
POST /api/tickets/{id}/admin-unbook     # Admin unbook for driver
DELETE /api/tickets/{id}                # Delete ticket (Admin)
```

### 🚚 Assignment Service (Port 8084)
```http
GET  /api/assignments                   # Get all assignments
GET  /api/assignments/{id}              # Get assignment by ID
GET  /api/assignments/zone/{zone}       # Get assignments by zone
GET  /api/assignments/driver/{id}       # Get driver's assignments
GET  /api/assignments/available         # Get available assignments
GET  /api/assignments/available/zone/{zone}?targetDate=YYYY-MM-DD # Available by zone/date
POST /api/assignments                   # Create assignment (Admin)
POST /api/assignments/{id}/claim        # 🔥 Claim assignment (Driver)
POST /api/assignments/{id}/unclaim      # 🔥 Unclaim assignment (Driver)  
POST /api/assignments/{id}/admin-claim  # 🔥 Admin claim for driver
POST /api/assignments/{id}/admin-unclaim # 🔥 Admin unclaim for driver
DELETE /api/assignments/{id}            # Delete assignment (Admin)
```

## 🔥 Key Assignment Claim/Unclaim APIs

### Driver Claim Assignment
```http
POST /api/assignments/{assignmentId}/claim
Content-Type: application/json

{
  "driverId": 2,
  "ticketId": 1  
}
```

### Driver Unclaim Assignment
```http
POST /api/assignments/{assignmentId}/unclaim
Content-Type: application/json

{
  "driverId": 2
}
```

### Admin Claim Assignment for Driver
```http
POST /api/assignments/{assignmentId}/admin-claim
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "driverId": 3,
  "ticketId": 2
}
```

### Admin Unclaim Assignment for Driver  
```http
POST /api/assignments/{assignmentId}/admin-unclaim
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "driverId": 3
}
```

## Swagger Documentation
Each service provides interactive API documentation:
- Auth: http://localhost:8081/swagger-ui.html
- Booking: http://localhost:8082/swagger-ui.html  
- Tickets: http://localhost:8083/swagger-ui.html
- Assignments: http://localhost:8084/swagger-ui.html

## Workflow Example
1. **Admin creates booking session** with target drivers and time windows
2. **Admin creates tickets** for the session with different zones  
3. **Driver books tickets** in their allowed zones during booking window
4. **Driver claims assignments** that match their booked ticket zones
5. **All operations generate Kafka events** for audit and notifications

## Testing
Use `test-assignment-apis.bat` to test the claim/unclaim functionality.