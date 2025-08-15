package com.jitsu.assignment.repository;

import com.jitsu.common.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByBookedByDriverId(Long driverId);
    
    @Query("SELECT DISTINCT t.zone FROM Ticket t WHERE t.bookedByDriverId = :driverId")
    List<String> findDistinctZonesByBookedByDriverId(Long driverId);
}