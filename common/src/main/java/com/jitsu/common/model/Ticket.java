package com.jitsu.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String zone;
    
    @NotNull
    private LocalDate targetDate;
    
    @NotNull
    private Long bookingSessionId;
    
    private Long bookedByDriverId;
    
    private LocalDateTime bookedAt;
    
    @NotNull
    private LocalDateTime endBookingTime;
    
    public Ticket() {}
    
    public Ticket(String zone, LocalDate targetDate, Long bookingSessionId, LocalDateTime endBookingTime) {
        this.zone = zone;
        this.targetDate = targetDate;
        this.bookingSessionId = bookingSessionId;
        this.endBookingTime = endBookingTime;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    
    public Long getBookingSessionId() { return bookingSessionId; }
    public void setBookingSessionId(Long bookingSessionId) { this.bookingSessionId = bookingSessionId; }
    
    public Long getBookedByDriverId() { return bookedByDriverId; }
    public void setBookedByDriverId(Long bookedByDriverId) { this.bookedByDriverId = bookedByDriverId; }
    
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
    
    public LocalDateTime getEndBookingTime() { return endBookingTime; }
    public void setEndBookingTime(LocalDateTime endBookingTime) { this.endBookingTime = endBookingTime; }
    
    public boolean isBooked() {
        return bookedByDriverId != null;
    }
}