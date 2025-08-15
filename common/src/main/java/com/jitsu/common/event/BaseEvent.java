package com.jitsu.common.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BookingSessionEvent.class, name = "booking_session"),
    @JsonSubTypes.Type(value = TicketEvent.class, name = "ticket"),
    @JsonSubTypes.Type(value = AssignmentEvent.class, name = "assignment")
})
public abstract class BaseEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String userId;
    private EventType eventType;
    
    public BaseEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseEvent(String eventId, String userId, EventType eventType) {
        this();
        this.eventId = eventId;
        this.userId = userId;
        this.eventType = eventType;
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    
    public enum EventType {
        CREATED, UPDATED, DELETED, BOOKED, UNBOOKED, CLAIMED, UNCLAIMED
    }
}