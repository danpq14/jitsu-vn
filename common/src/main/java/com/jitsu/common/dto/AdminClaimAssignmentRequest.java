package com.jitsu.common.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for admin assignment claim requests (admin claiming assignment for driver with ticket)
 */
public class AdminClaimAssignmentRequest {
    
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    
    public AdminClaimAssignmentRequest() {}
    
    public AdminClaimAssignmentRequest(Long driverId, Long ticketId) {
        this.driverId = driverId;
        this.ticketId = ticketId;
    }
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}