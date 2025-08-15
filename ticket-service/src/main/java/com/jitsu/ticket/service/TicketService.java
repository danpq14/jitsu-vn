package com.jitsu.ticket.service;

import com.jitsu.common.event.BaseEvent;
import com.jitsu.common.event.TicketEvent;
import com.jitsu.common.exception.BookingException;
import com.jitsu.common.exception.ResourceNotFoundException;
import com.jitsu.common.model.Ticket;
import com.jitsu.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }
    
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
    }
    
    public List<Ticket> findByBookingSessionId(Long bookingSessionId) {
        return ticketRepository.findByBookingSessionId(bookingSessionId);
    }
    
    public List<Ticket> findByDriverId(Long driverId) {
        return ticketRepository.findByBookedByDriverId(driverId);
    }
    
    public List<Ticket> findAvailableByBookingSession(Long bookingSessionId) {
        return ticketRepository.findByBookingSessionIdAndBookedByDriverIdIsNull(bookingSessionId);
    }
    
    public Ticket create(Ticket ticket) {
        // Ensure end_booking_time is set - if not provided, set a default
        if (ticket.getEndBookingTime() == null) {
            ticket.setEndBookingTime(LocalDateTime.now().plusHours(24)); // Default 24 hours
        }
        Ticket saved = ticketRepository.save(ticket);
        sendEvent(saved, BaseEvent.EventType.CREATED, "system");
        return saved;
    }
    
    @Transactional
    public Ticket bookTicket(Long ticketId, Long driverId) {
        Ticket ticket = findById(ticketId);
        
        if (ticket.isBooked()) {
            throw new BookingException("Ticket is already booked");
        }
        
        ticket.setBookedByDriverId(driverId);
        ticket.setBookedAt(LocalDateTime.now());
        
        Ticket saved = ticketRepository.save(ticket);
        sendEvent(saved, BaseEvent.EventType.BOOKED, driverId.toString());
        return saved;
    }
    
    @Transactional
    public Ticket unbookTicket(Long ticketId, Long driverId) {
        Ticket ticket = findById(ticketId);
        
        if (!ticket.isBooked() || !ticket.getBookedByDriverId().equals(driverId)) {
            throw new BookingException("Cannot unbook ticket - not booked by this driver");
        }
        
        ticket.setBookedByDriverId(null);
        ticket.setBookedAt(null);
        
        Ticket saved = ticketRepository.save(ticket);
        sendEvent(saved, BaseEvent.EventType.UNBOOKED, driverId.toString());
        return saved;
    }
    
    public void delete(Long id) {
        Ticket ticket = findById(id);
        ticketRepository.delete(ticket);
        sendEvent(ticket, BaseEvent.EventType.DELETED, "system");
    }
    
    private void sendEvent(Ticket ticket, BaseEvent.EventType eventType, String userId) {
        TicketEvent event = new TicketEvent(
            UUID.randomUUID().toString(),
            userId,
            eventType,
            ticket
        );
        kafkaTemplate.send("ticket-events", event);
    }
}