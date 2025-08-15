package com.jitsu.common.event;

import com.jitsu.common.model.BookingSession;

public class BookingSessionEvent extends BaseEvent {
    private BookingSession bookingSession;
    
    public BookingSessionEvent() {}
    
    public BookingSessionEvent(String eventId, String userId, EventType eventType, BookingSession bookingSession) {
        super(eventId, userId, eventType);
        this.bookingSession = bookingSession;
    }
    
    public BookingSession getBookingSession() { return bookingSession; }
    public void setBookingSession(BookingSession bookingSession) { this.bookingSession = bookingSession; }
}