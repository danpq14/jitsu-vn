package com.jitsu.ticket.repository;

import com.jitsu.common.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByBookingSessionId(Long bookingSessionId);
    List<Ticket> findByBookedByDriverId(Long driverId);
    List<Ticket> findByZone(String zone);
    List<Ticket> findByTargetDate(LocalDate targetDate);
    List<Ticket> findByBookingSessionIdAndBookedByDriverIdIsNull(Long bookingSessionId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.bookedByDriverId = :driverId AND t.bookingSessionId = :bookingSessionId")
    long countByDriverIdAndBookingSessionId(Long driverId, Long bookingSessionId);
    
    // Find unclaimed tickets where booking time has ended
    @Query("SELECT t FROM Ticket t WHERE t.endBookingTime < :currentTime AND t.bookedByDriverId IS NULL")
    List<Ticket> findUnclaimedTicketsAfterBookingEnd(LocalDateTime currentTime);
}