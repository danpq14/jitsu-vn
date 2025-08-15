package com.jitsu.booking.controller;

import com.jitsu.booking.service.BookingSessionService;
import com.jitsu.common.model.BookingSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.jitsu.common.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-sessions")
@Tag(name = "Booking Sessions", description = "Booking Session Management API")
public class BookingSessionController {
    
    @Autowired
    private BookingSessionService bookingSessionService;
    
    @GetMapping
    @Operation(summary = "Get all booking sessions")
    public ResponseEntity<List<BookingSession>> getAllBookingSessions() {
        return ResponseEntity.ok(bookingSessionService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking session by ID")
    public ResponseEntity<BookingSession> getBookingSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingSessionService.findById(id));
    }
    
    @GetMapping("/region/{regionCode}")
    @Operation(summary = "Get booking sessions by region code")
    public ResponseEntity<List<BookingSession>> getBookingSessionsByRegion(@PathVariable String regionCode) {
        return ResponseEntity.ok(bookingSessionService.findByRegionCode(regionCode));
    }
    
    @GetMapping("/driver")
    @Operation(summary = "Get booking sessions for current driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<BookingSession>> getBookingSessionsForCurrentDriver() {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingSessionService.findByDriverId(driverId));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new booking session (Admin only)")
    public ResponseEntity<BookingSession> createBookingSession(@RequestBody BookingSession bookingSession) {
        return ResponseEntity.ok(bookingSessionService.create(bookingSession));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update booking session (Admin only)")
    public ResponseEntity<BookingSession> updateBookingSession(@PathVariable Long id, @RequestBody BookingSession bookingSession) {
        return ResponseEntity.ok(bookingSessionService.update(id, bookingSession));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete booking session (Admin only)")
    public ResponseEntity<Void> deleteBookingSession(@PathVariable Long id) {
        bookingSessionService.delete(id);
        return ResponseEntity.ok().build();
    }
}