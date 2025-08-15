package com.jitsu.common.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for requests that only need driver ID (used by admin operations)
 */
public class DriverIdRequest {
    
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    public DriverIdRequest() {}
    
    public DriverIdRequest(Long driverId) {
        this.driverId = driverId;
    }
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}