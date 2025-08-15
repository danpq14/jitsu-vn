package com.jitsu.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String zone;
    
    @NotNull
    private LocalDate targetDate;
    
    @NotNull
    private String description;
    
    private Long claimedByDriverId;
    
    private Long claimedTicketId;
    
    private LocalDateTime claimedAt;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status = Status.AVAILABLE;
    
    public Assignment() {}
    
    public Assignment(String zone, LocalDate targetDate, String description) {
        this.zone = zone;
        this.targetDate = targetDate;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getClaimedByDriverId() { return claimedByDriverId; }
    public void setClaimedByDriverId(Long claimedByDriverId) { this.claimedByDriverId = claimedByDriverId; }
    
    public Long getClaimedTicketId() { return claimedTicketId; }
    public void setClaimedTicketId(Long claimedTicketId) { this.claimedTicketId = claimedTicketId; }
    
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public boolean isClaimed() {
        return claimedByDriverId != null;
    }
    
    public enum Status {
        AVAILABLE, CLAIMED, COMPLETED
    }
}