package com.jitsu.eventprocessor.service;

import com.jitsu.common.event.AssignmentEvent;
import com.jitsu.common.event.BookingSessionEvent;
import com.jitsu.common.event.TicketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventProcessorService.class);
    
    @KafkaListener(topics = "booking-events", groupId = "event-processor-group")
    public void processBookingEvent(BookingSessionEvent event) {
        logger.info("Processing booking session event: {} for session ID: {}", 
                   event.getEventType(), event.getBookingSession().getId());
        //Add notification logic, audit logging, etc.
    }
    
    @KafkaListener(topics = "ticket-events", groupId = "event-processor-group")
    public void processTicketEvent(TicketEvent event) {
        logger.info("Processing ticket event: {} for ticket ID: {}", 
                   event.getEventType(), event.getTicket().getId());
        //Add notification logic, audit logging, etc.
    }
    
    @KafkaListener(topics = "assignment-events", groupId = "event-processor-group")
    public void processAssignmentEvent(AssignmentEvent event) {
        logger.info("Processing assignment event: {} for assignment ID: {}", 
                   event.getEventType(), event.getAssignment().getId());
        //Add notification logic, audit logging, etc.
    }
}