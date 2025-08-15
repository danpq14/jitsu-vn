package com.jitsu.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "booking_sessions")
public class BookingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String regionCode;
    
    @NotNull
    private String name;
    
    @NotNull
    private LocalDate targetDate;
    
    @ElementCollection
    @CollectionTable(name = "booking_session_drivers", joinColumns = @JoinColumn(name = "booking_session_id"))
    @Column(name = "driver_id")
    private Set<Long> targetDrivers;
    
    @NotNull
    private LocalDateTime startBookingTime;
    
    @NotNull
    private LocalDateTime endBookingTime;
    
    @NotNull
    private LocalDateTime latestCancellationTime;
    
    @NotNull
    private Integer maxTicketsPerDriver;
    
    public BookingSession() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    
    public Set<Long> getTargetDrivers() { return targetDrivers; }
    public void setTargetDrivers(Set<Long> targetDrivers) { this.targetDrivers = targetDrivers; }
    
    public LocalDateTime getStartBookingTime() { return startBookingTime; }
    public void setStartBookingTime(LocalDateTime startBookingTime) { this.startBookingTime = startBookingTime; }
    
    public LocalDateTime getEndBookingTime() { return endBookingTime; }
    public void setEndBookingTime(LocalDateTime endBookingTime) { this.endBookingTime = endBookingTime; }
    
    public LocalDateTime getLatestCancellationTime() { return latestCancellationTime; }
    public void setLatestCancellationTime(LocalDateTime latestCancellationTime) { this.latestCancellationTime = latestCancellationTime; }
    
    public Integer getMaxTicketsPerDriver() { return maxTicketsPerDriver; }
    public void setMaxTicketsPerDriver(Integer maxTicketsPerDriver) { this.maxTicketsPerDriver = maxTicketsPerDriver; }
}