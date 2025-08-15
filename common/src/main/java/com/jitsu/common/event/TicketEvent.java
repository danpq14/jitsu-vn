package com.jitsu.common.event;

import com.jitsu.common.model.Ticket;

public class TicketEvent extends BaseEvent {
    private Ticket ticket;
    
    public TicketEvent() {}
    
    public TicketEvent(String eventId, String userId, EventType eventType, Ticket ticket) {
        super(eventId, userId, eventType);
        this.ticket = ticket;
    }
    
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}