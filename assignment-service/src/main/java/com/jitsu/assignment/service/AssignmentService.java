package com.jitsu.assignment.service;

import com.jitsu.assignment.repository.AssignmentRepository;
import com.jitsu.assignment.repository.TicketRepository;
import com.jitsu.common.event.AssignmentEvent;
import com.jitsu.common.event.BaseEvent;
import com.jitsu.common.exception.BookingException;
import com.jitsu.common.exception.ResourceNotFoundException;
import com.jitsu.common.model.Assignment;
import com.jitsu.common.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }
    
    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + id));
    }
    
    public List<Assignment> findByZone(String zone) {
        return assignmentRepository.findByZone(zone);
    }
    
    public List<Assignment> findByDriverId(Long driverId) {
        return assignmentRepository.findByClaimedByDriverId(driverId);
    }
    
    public List<Assignment> findAvailable() {
        return assignmentRepository.findByClaimedByDriverIdIsNull();
    }
    
    public List<Assignment> findAvailableByZoneAndDate(String zone, LocalDate targetDate) {
        return assignmentRepository.findByZoneAndTargetDateAndClaimedByDriverIdIsNull(zone, targetDate);
    }
    
    public List<Assignment> findAvailableForDriver(Long driverId) {
        // Get distinct zones from driver's booked tickets
        List<String> driverTicketZones = ticketRepository.findDistinctZonesByBookedByDriverId(driverId);
        
        if (driverTicketZones.isEmpty()) {
            return List.of(); // No tickets booked, no assignments available
        }
        
        // Find available assignments in those zones
        return assignmentRepository.findByZoneInAndClaimedByDriverIdIsNull(driverTicketZones);
    }
    
    public Assignment create(Assignment assignment) {
        Assignment saved = assignmentRepository.save(assignment);
        sendEvent(saved, BaseEvent.EventType.CREATED, "system");
        return saved;
    }
    
    @Transactional
    public Assignment claimAssignment(Long assignmentId, Long driverId, Long ticketId) {
        Assignment assignment = findById(assignmentId);
        
        if (assignment.isClaimed()) {
            throw new BookingException("Assignment is already claimed");
        }
        
        // Validate ticket ownership, zone matching, and booking status
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        
        // 1. Validate the ticket belongs to the driver
        if (!ticket.isBooked() || !ticket.getBookedByDriverId().equals(driverId)) {
            throw new BookingException("Ticket " + ticketId + " is not booked by driver " + driverId);
        }
        
        // 2. Validate the ticket zone matches the assignment zone
        if (!ticket.getZone().equals(assignment.getZone())) {
            throw new BookingException("Ticket zone (" + ticket.getZone() + 
                ") does not match assignment zone (" + assignment.getZone() + ")");
        }
        
        // 3. Validate the ticket is not expired
        if (ticket.getEndBookingTime().isBefore(LocalDateTime.now())) {
            throw new BookingException("Ticket " + ticketId + " has expired");
        }
        
        // 4. Validate the ticket target date matches assignment target date
        if (!ticket.getTargetDate().equals(assignment.getTargetDate())) {
            throw new BookingException("Ticket target date (" + ticket.getTargetDate() + 
                ") does not match assignment target date (" + assignment.getTargetDate() + ")");
        }
        
        assignment.setClaimedByDriverId(driverId);
        assignment.setClaimedTicketId(ticketId);
        assignment.setClaimedAt(LocalDateTime.now());
        assignment.setStatus(Assignment.Status.CLAIMED);
        
        Assignment saved = assignmentRepository.save(assignment);
        sendEvent(saved, BaseEvent.EventType.CLAIMED, driverId.toString());
        return saved;
    }
    
    @Transactional
    public Assignment unclaimAssignment(Long assignmentId, Long driverId) {
        Assignment assignment = findById(assignmentId);
        
        if (!assignment.isClaimed() || !assignment.getClaimedByDriverId().equals(driverId)) {
            throw new BookingException("Cannot unclaim assignment - not claimed by this driver");
        }
        
        assignment.setClaimedByDriverId(null);
        assignment.setClaimedTicketId(null);
        assignment.setClaimedAt(null);
        assignment.setStatus(Assignment.Status.AVAILABLE);
        
        Assignment saved = assignmentRepository.save(assignment);
        sendEvent(saved, BaseEvent.EventType.UNCLAIMED, driverId.toString());
        return saved;
    }
    
    public void delete(Long id) {
        Assignment assignment = findById(id);
        assignmentRepository.delete(assignment);
        sendEvent(assignment, BaseEvent.EventType.DELETED, "system");
    }
    
    private void sendEvent(Assignment assignment, BaseEvent.EventType eventType, String userId) {
        AssignmentEvent event = new AssignmentEvent(
            UUID.randomUUID().toString(),
            userId,
            eventType,
            assignment
        );
        kafkaTemplate.send("assignment-events", event);
    }
}