package com.jitsu.booking.repository;

import com.jitsu.common.model.BookingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingSessionRepository extends JpaRepository<BookingSession, Long> {
    List<BookingSession> findByRegionCode(String regionCode);
    List<BookingSession> findByTargetDate(LocalDate targetDate);
    
    @Query("SELECT bs FROM BookingSession bs WHERE :driverId MEMBER OF bs.targetDrivers")
    List<BookingSession> findByTargetDriversContaining(Long driverId);
}