package com.jitsu.common.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for assignment claim requests (driver claiming assignment with ticket)
 */
public class ClaimAssignmentRequest {
    
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    
    public ClaimAssignmentRequest() {}
    
    public ClaimAssignmentRequest(Long ticketId) {
        this.ticketId = ticketId;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}