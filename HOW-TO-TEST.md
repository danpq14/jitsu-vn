# How to Test Jitsu Logistics System

This guide walks you through testing the complete Jitsu logistics booking system with realistic workflows for both Admin and Driver users.

## 🚀 Prerequisites

1. **Start the system:**
   ```bash
   start.bat  # Windows
   ./start.sh # Linux/Mac
   ```

2. **Wait for all services to be ready (~2 minutes)**
   ```bash
   docker-compose ps  # Check all services are "Up (healthy)"
   ```

3. **Access points:**
   - **API Gateway:** http://localhost:8080
   - **Swagger UI:** http://localhost:808[2-5]/swagger-ui.html

## 🔐 Test Users

- **Admin:** `admin` / `admin123`
- **Drivers:** `driver1`, `driver2`, `driver3`, `driver4` / `driver123`

---

## 📋 Admin Test Plan

### **Step 1: Verify Admin Authentication**
```bash
# Admin authentication is handled via Basic Auth - no separate login endpoint needed
# All API calls use: -u admin:admin123
echo "Admin authentication uses Basic Auth with username 'admin' and password 'admin123'"
```

### **Step 2: View Existing Booking Sessions**
```bash
# Get all booking sessions
curl -X GET http://localhost:8082/api/booking-sessions \
  -u admin:admin123

# Expected: Array of existing sessions (should have SFO and LAX sessions from sample data)
```

### **Step 3: Create New Booking Session**
```bash
# Create a new booking session for Chicago
curl -X POST http://localhost:8082/api/booking-sessions \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "regionCode": "CHI",
    "name": "Chicago Morning Routes",
    "targetDate": "2025-08-16",
    "targetDrivers": [2, 3, 4],
    "startBookingTime": "2025-08-15T08:00:00",
    "endBookingTime": "2025-08-15T18:00:00",
    "latestCancellationTime": "2025-08-15T16:00:00",
    "maxTicketsPerDriver": 2
  }'

# Expected: Created booking session with ID (note the ID for next steps)
```

### **Step 4: Add Tickets to Booking Session**
```bash
# Add tickets for different zones (replace {sessionId} with actual ID from step 3)
curl -X POST http://localhost:8083/api/tickets \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "zone": "Z1",
    "targetDate": "2025-08-16",
    "bookingSessionId": 3,
    "endBookingTime": "2025-08-15T18:00:00"
  }'

curl -X POST http://localhost:8083/api/tickets \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "zone": "Z2",
    "targetDate": "2025-08-16",
    "bookingSessionId": 3,
    "endBookingTime": "2025-08-15T18:00:00"
  }'

# Expected: Created tickets with IDs
```

### **Step 5: Add Assignments**
```bash
# Add assignments for the zones
curl -X POST http://localhost:8084/api/assignments \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "zone": "Z1",
    "targetDate": "2025-08-16",
    "description": "Chicago downtown delivery route"
  }'

curl -X POST http://localhost:8084/api/assignments \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "zone": "Z2", 
    "targetDate": "2025-08-16",
    "description": "Chicago suburbs pickup route"
  }'

# Expected: Created assignments with IDs
```

### **Step 6: Admin Override - Book Ticket for Driver**
```bash
# Admin books ticket ID 7 for driver2 (ID: 3)
curl -X POST http://localhost:8083/api/tickets/7/admin-book \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 3}'

# Expected: Ticket booked successfully by admin
```

### **Step 7: Admin Override - Claim Assignment for Driver**
```bash
# Admin claims assignment ID 7 for driver2 using ticket ID 7
curl -X POST http://localhost:8084/api/assignments/7/admin-claim \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 3, "ticketId": 7}'

# Expected: Assignment claimed successfully by admin
```

### **Step 8: Monitor System Events**
```bash
# Check event processor logs
docker logs jitsu-event-processor

# Expected: See Kafka event logs for all operations (booking, claiming, etc.)
```

---

## 🚛 Driver Test Plan

### **Step 1: Driver Authentication**
```bash
# Driver authentication is handled via Basic Auth - no separate login endpoint needed
# All API calls use: -u driver1:driver123 (or driver2, driver3, driver4)
echo "Driver authentication uses Basic Auth with username 'driver1' and password 'driver123'"
```

### **Step 2: View Available Booking Sessions**
```bash
# Driver1 checks their available booking sessions (automatically gets current user)
curl -X GET http://localhost:8082/api/booking-sessions/driver \
  -u driver1:driver123

# Expected: Array of booking sessions where driver1 is a target driver
```

### **Step 3: View Available Tickets**
```bash
# Check available tickets for booking session 1 (SFO session from sample data)
curl -X GET http://localhost:8083/api/tickets/booking-session/1/available \
  -u driver1:driver123

# Expected: Array of unbooked tickets
```

### **Step 4: Book a Ticket**
```bash
# Driver1 books ticket ID 1 (Z1 zone)
curl -X POST http://localhost:8083/api/tickets/1/book \
  -u driver1:driver123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 2}'

# Expected: Ticket successfully booked
```

### **Step 5: View Driver's Booked Tickets**
```bash
# Check what tickets current driver has booked
curl -X GET http://localhost:8083/api/tickets/driver \
  -u driver1:driver123

# Expected: Array of tickets booked by driver1
```

### **Step 6: Find Matching Assignments**
```bash
# Look for assignments in Z1 zone (matching the booked ticket)
curl -X GET "http://localhost:8084/api/assignments/available/zone/Z1?targetDate=2025-08-15" \
  -u driver1:driver123

# Expected: Available assignments in Z1 zone
```

### **Step 7: Claim an Assignment**
```bash
# Driver1 claims assignment ID 1 using ticket ID 1
curl -X POST http://localhost:8084/api/assignments/1/claim \
  -u driver1:driver123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 2, "ticketId": 1}'

# Expected: Assignment successfully claimed
```

### **Step 8: View Driver's Claimed Assignments**
```bash
# Check what assignments current driver has claimed
curl -X GET http://localhost:8084/api/assignments/driver \
  -u driver1:driver123

# Expected: Array of assignments claimed by driver1
```

### **Step 9: Unclaim Assignment (if needed)**
```bash
# Driver1 unclaims assignment ID 1
curl -X POST http://localhost:8084/api/assignments/1/unclaim \
  -u driver1:driver123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 2}'

# Expected: Assignment successfully unclaimed
```

### **Step 10: Unbook Ticket (if needed)**
```bash
# Driver1 unbooks ticket ID 1
curl -X POST http://localhost:8083/api/tickets/1/unbook \
  -u driver1:driver123 \
  -H "Content-Type: application/json" \
  -d '{"driverId": 2}'

# Expected: Ticket successfully unbooked
```

---

### **Swagger UI Testing**
Visit these URLs in your browser for interactive testing:
- **Booking Service:** http://localhost:8082/swagger-ui.html  
- **Ticket Service:** http://localhost:8083/swagger-ui.html
- **Assignment Service:** http://localhost:8084/swagger-ui.html
- **Event Processor:** http://localhost:8085/swagger-ui.html

---

## 📊 System Monitoring

### **Check Service Health**
```bash
docker-compose ps                    # Service status
docker-compose logs -f auth-service  # View specific service logs
docker logs jitsu-event-processor    # View event processing logs
```

### **Database Verification**
```bash
# Connect to PostgreSQL and verify data
docker exec -it jitsu-postgres psql -U jitsu_user -d jitsu_db -c "SELECT * FROM users;"
docker exec -it jitsu-postgres psql -U jitsu_user -d jitsu_db -c "SELECT * FROM booking_sessions;"
```

---

## ✅ Expected Test Results

### **Successful Admin Flow:**
1. ✅ Admin can login successfully
2. ✅ Admin can create booking sessions
3. ✅ Admin can add tickets to sessions
4. ✅ Admin can add assignments  
5. ✅ Admin can book/unbook tickets for drivers
6. ✅ Admin can claim/unclaim assignments for drivers
7. ✅ All operations generate Kafka events

### **Successful Driver Flow:**
1. ✅ Driver can login successfully
2. ✅ Driver can see their available booking sessions
3. ✅ Driver can see available tickets in sessions
4. ✅ Driver can book tickets in their allowed zones
5. ✅ Driver can see assignments matching their ticket zones
6. ✅ Driver can claim assignments using their tickets
7. ✅ Driver can unclaim assignments and unbook tickets
8. ✅ All operations generate Kafka events

### **Security Validation:**
- ✅ Driver endpoints work without admin role
- ✅ Admin-only endpoints reject driver access
- ✅ Authentication required for all business endpoints
- ✅ Swagger UI accessible without authentication

---

## 🚨 Troubleshooting

### **Common Issues:**
1. **Service not healthy:** Wait longer or check `docker-compose logs [service-name]`
2. **Authentication fails:** Verify username/password and BCrypt hashing
3. **Kafka events missing:** Check `docker logs jitsu-event-processor`
4. **Database empty:** Verify PostgreSQL init scripts ran: `docker logs jitsu-postgres`

### **Reset System:**
```bash
docker-compose down    # Stop everything
docker volume rm jitsu_postgres_data  # Clear database (optional)
start.bat              # Fresh start
```

This completes the comprehensive testing of your Jitsu logistics booking system! 🎉