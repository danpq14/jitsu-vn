package com.jitsu.booking.service;

import com.jitsu.booking.repository.BookingSessionRepository;
import com.jitsu.common.event.BaseEvent;
import com.jitsu.common.event.BookingSessionEvent;
import com.jitsu.common.exception.ResourceNotFoundException;
import com.jitsu.common.model.BookingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingSessionService {
    
    @Autowired
    private BookingSessionRepository bookingSessionRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public List<BookingSession> findAll() {
        return bookingSessionRepository.findAll();
    }
    
    public BookingSession findById(Long id) {
        return bookingSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking session not found: " + id));
    }
    
    public List<BookingSession> findByRegionCode(String regionCode) {
        return bookingSessionRepository.findByRegionCode(regionCode);
    }
    
    public List<BookingSession> findByDriverId(Long driverId) {
        return bookingSessionRepository.findByTargetDriversContaining(driverId);
    }
    
    public BookingSession create(BookingSession bookingSession) {
        BookingSession saved = bookingSessionRepository.save(bookingSession);
        sendEvent(saved, BaseEvent.EventType.CREATED, "system");
        return saved;
    }
    
    public BookingSession update(Long id, BookingSession bookingSession) {
        BookingSession existing = findById(id);
        existing.setRegionCode(bookingSession.getRegionCode());
        existing.setName(bookingSession.getName());
        existing.setTargetDate(bookingSession.getTargetDate());
        existing.setTargetDrivers(bookingSession.getTargetDrivers());
        existing.setStartBookingTime(bookingSession.getStartBookingTime());
        existing.setEndBookingTime(bookingSession.getEndBookingTime());
        existing.setLatestCancellationTime(bookingSession.getLatestCancellationTime());
        existing.setMaxTicketsPerDriver(bookingSession.getMaxTicketsPerDriver());
        
        BookingSession saved = bookingSessionRepository.save(existing);
        sendEvent(saved, BaseEvent.EventType.UPDATED, "system");
        return saved;
    }
    
    public void delete(Long id) {
        BookingSession bookingSession = findById(id);
        bookingSessionRepository.delete(bookingSession);
        sendEvent(bookingSession, BaseEvent.EventType.DELETED, "system");
    }
    
    private void sendEvent(BookingSession bookingSession, BaseEvent.EventType eventType, String userId) {
        BookingSessionEvent event = new BookingSessionEvent(
            UUID.randomUUID().toString(),
            userId,
            eventType,
            bookingSession
        );
        kafkaTemplate.send("booking-events", event);
    }
}