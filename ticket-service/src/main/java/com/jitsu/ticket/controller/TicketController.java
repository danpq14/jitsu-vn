package com.jitsu.ticket.controller;

import com.jitsu.common.dto.DriverIdRequest;
import com.jitsu.common.model.Ticket;
import com.jitsu.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.jitsu.common.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket Management API")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @GetMapping
    @Operation(summary = "Get all tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }
    
    @GetMapping("/booking-session/{sessionId}")
    @Operation(summary = "Get tickets by booking session")
    public ResponseEntity<List<Ticket>> getTicketsByBookingSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ticketService.findByBookingSessionId(sessionId));
    }
    
    @GetMapping("/booking-session/{sessionId}/available")
    @Operation(summary = "Get available tickets by booking session")
    public ResponseEntity<List<Ticket>> getAvailableTicketsByBookingSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ticketService.findAvailableByBookingSession(sessionId));
    }
    
    @GetMapping("/driver")
    @Operation(summary = "Get tickets booked by current driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<Ticket>> getTicketsForCurrentDriver() {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(ticketService.findByDriverId(driverId));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new ticket (Admin only)")
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.create(ticket));
    }
    
    @PostMapping("/{ticketId}/book")
    @Operation(summary = "Book a ticket")
    public ResponseEntity<Ticket> bookTicket(@PathVariable Long ticketId) {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(ticketService.bookTicket(ticketId, driverId));
    }
    
    @PostMapping("/{ticketId}/unbook")
    @Operation(summary = "Unbook a ticket")
    public ResponseEntity<Ticket> unbookTicket(@PathVariable Long ticketId) {
        Long driverId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(ticketService.unbookTicket(ticketId, driverId));
    }
    
    @PostMapping("/{ticketId}/admin-book")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin book ticket for driver")
    public ResponseEntity<Ticket> adminBookTicket(@PathVariable Long ticketId, @RequestBody DriverIdRequest request) {
        Long driverId = request.getDriverId();
        return ResponseEntity.ok(ticketService.bookTicket(ticketId, driverId));
    }
    
    @PostMapping("/{ticketId}/admin-unbook")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin unbook ticket for driver")
    public ResponseEntity<Ticket> adminUnbookTicket(@PathVariable Long ticketId, @RequestBody DriverIdRequest request) {
        Long driverId = request.getDriverId();
        return ResponseEntity.ok(ticketService.unbookTicket(ticketId, driverId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete ticket (Admin only)")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.ok().build();
    }
}