# Driver Ticket Booking - Data Flow Diagram

## Overview
This diagram shows the complete data flow when a driver books a ticket in the Jitsu logistics system.

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           DRIVER TICKET BOOKING FLOW                                   │
└─────────────────────────────────────────────────────────────────────────────────────┘

    Driver                API Gateway           Ticket Service          Database           Kafka
      │                        │                      │                    │               │
      │ 1. POST /api/tickets/  │                      │                    │               │
      │    {ticketId}/book     │                      │                    │               │
      │ + Basic Auth           │                      │                    │               │
      │ (driver1:driver123)    │                      │                    │               │
      ├───────────────────────►│                      │                    │               │
      │                        │ 2. Route to          │                    │               │
      │                        │    Ticket Service    │                    │               │
      │                        │    (port 8083)       │                    │               │
      │                        ├─────────────────────►│                    │               │
      │                        │                      │ 3. Security Check  │               │
      │                        │                      │    - Basic Auth    │               │
      │                        │                      │    - Role: DRIVER  │               │
      │                        │                      │                    │               │
      │                        │                      │ 4. Find Ticket     │               │
      │                        │                      │    by ID           │               │
      │                        │                      ├───────────────────►│               │
      │                        │                      │                    │               │
      │                        │                      │ 5. Ticket Entity   │               │
      │                        │                      │    Retrieved        │               │
      │                        │                      │◄───────────────────┤               │
      │                        │                      │                    │               │
      │                        │                      │ 6. Business Logic  │               │
      │                        │                      │    Validation:      │               │
      │                        │                      │    ✓ Ticket exists │               │
      │                        │                      │    ✓ Not booked    │               │
      │                        │                      │    ✓ Driver in     │               │
      │                        │                      │      target list   │               │
      │                        │                      │    ✓ Within booking│               │
      │                        │                      │      time window   │               │
      │                        │                      │    ✓ Driver hasn't │               │
      │                        │                      │      exceeded max  │               │
      │                        │                      │      tickets limit │               │
      │                        │                      │                    │               │
      │                        │                      │ 7. Update Ticket   │               │
      │                        │                      │    SET booked_by = │               │
      │                        │                      │        {driverId}  │               │
      │                        │                      │    SET status =    │               │
      │                        │                      │        'BOOKED'    │               │
      │                        │                      │    SET booked_at = │               │
      │                        │                      │        NOW()       │               │
      │                        │                      ├───────────────────►│               │
      │                        │                      │                    │               │
      │                        │                      │ 8. DB Transaction  │               │
      │                        │                      │    Committed       │               │
      │                        │                      │◄───────────────────┤               │
      │                        │                      │                    │               │
      │                        │                      │ 9. Publish Kafka   │               │
      │                        │                      │    Event:          │               │
      │                        │                      │    "ticket.booked" │               │
      │                        │                      │    {               │               │
      │                        │                      │      ticketId,     │               │
      │                        │                      │      driverId,     │               │
      │                        │                      │      zone,         │               │
      │                        │                      │      targetDate,   │               │
      │                        │                      │      timestamp     │               │
      │                        │                      │    }               │               │
      │                        │                      ├───────────────────────────────────►│
      │                        │                      │                    │               │
      │                        │ 10. HTTP 200 OK     │                    │               │
      │                        │     Updated Ticket  │                    │               │
      │                        │     Entity          │                    │               │
      │                        │◄─────────────────────┤                    │               │
      │ 11. Success Response   │                      │                    │               │
      │     {                  │                      │                    │               │
      │       "id": 1,         │                      │                    │               │
      │       "zone": "Z1",    │                      │                    │               │
      │       "status": "BOOKED│                      │                    │               │
      │       "bookedBy": 2,   │                      │                    │               │
      │       "bookedAt": "...",                      │                    │               │
      │       ...              │                      │                    │               │
      │     }                  │                      │                    │               │
      │◄───────────────────────┤                      │                    │               │
      │                        │                      │                    │               │

                                            ┌─────────────────────────────────┐
                                            │         Event Processor         │
                                            │                                 │
                                            │ 12. Consumes "ticket.booked"   │
                                            │     event from Kafka            │
                                            │                                 │
                                            │ 13. Logs booking event          │
                                            │     (could trigger other        │
                                            │      business processes)        │
                                            │                                 │
                                            │ 14. Could update analytics,     │
                                            │     send notifications, etc.    │
                                            └─────────────────────────────────┘
```

## Database Schema Impact

### Before Booking:
```sql
tickets table:
┌────┬──────┬─────────────┬────────────┬───────────┬───────────┬──────────────────────┐
│ id │ zone │ target_date │ booking_.. │ status    │ booked_by │ booked_at            │
├────┼──────┼─────────────┼────────────┼───────────┼───────────┼──────────────────────┤
│ 1  │ Z1   │ 2025-08-15  │ 1          │ AVAILABLE │ NULL      │ NULL                 │
└────┴──────┴─────────────┴────────────┴───────────┴───────────┴──────────────────────┘
```

### After Booking:
```sql
tickets table:
┌────┬──────┬─────────────┬────────────┬───────────┬───────────┬──────────────────────┐
│ id │ zone │ target_date │ booking_.. │ status    │ booked_by │ booked_at            │
├────┼──────┼─────────────┼────────────┼───────────┼───────────┼──────────────────────┤
│ 1  │ Z1   │ 2025-08-15  │ 1          │ BOOKED    │ 2         │ 2025-08-14T10:30:00Z │
└────┴──────┴─────────────┴────────────┴───────────┴───────────┴──────────────────────┘
```

## Business Rules Enforced

1. **Authentication**: Driver must provide valid Basic Auth credentials
2. **Authorization**: Only users with DRIVER role can book tickets
3. **Ticket Availability**: Ticket must be in AVAILABLE status (not already booked)
4. **Target Driver Check**: Driver must be in the booking session's target_drivers list
5. **Time Window**: Current time must be within booking session's start/end booking time
6. **Max Tickets Limit**: Driver cannot exceed max_tickets_per_driver for that session
7. **Zone Consistency**: Booking creates foundation for later assignment claiming in same zone

## Event-Driven Architecture

The Kafka event `ticket.booked` enables:
- **Audit Trail**: All booking actions are logged
- **Decoupled Processing**: Other services can react to bookings
- **Analytics**: Track booking patterns and driver behavior
- **Notifications**: Could trigger notifications to admin/other drivers
- **Assignment Preparation**: Assignment service can prepare for potential claims

## API Request/Response

### Request:
```http
POST /api/tickets/1/book HTTP/1.1
Host: localhost:8083
Authorization: Basic ZHJpdmVyMTpkcml2ZXIxMjM=
Content-Type: application/json

{
  "driverId": 2
}
```

### Response:
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "zone": "Z1", 
  "targetDate": "2025-08-15",
  "bookingSessionId": 1,
  "status": "BOOKED",
  "bookedBy": 2,
  "bookedAt": "2025-08-14T10:30:00Z",
  "endBookingTime": "2025-08-15T18:00:00Z"
}
```

## Error Scenarios

| Error Condition | HTTP Status | Response |
|----------------|-------------|----------|
| Ticket not found | 404 | `{"error": "Ticket not found"}` |
| Already booked | 400 | `{"error": "Ticket already booked"}` |
| Driver not in target list | 403 | `{"error": "Driver not authorized for this session"}` |
| Outside booking window | 400 | `{"error": "Booking window closed"}` |
| Max tickets exceeded | 400 | `{"error": "Driver has reached maximum tickets"}` |
| Invalid authentication | 401 | `{"error": "Authentication required"}` |

This flow demonstrates the complete lifecycle of a ticket booking operation, including security, validation, persistence, and event publishing.