package com.jitsu.ticket.scheduler;

import com.jitsu.common.model.Ticket;
import com.jitsu.ticket.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class TicketCleanupScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketCleanupScheduler.class);
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void cleanupUnclaimedTickets() {
        logger.info("Starting cleanup of unclaimed tickets after booking end time");
        
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> expiredUnclaimedTickets = ticketRepository.findUnclaimedTicketsAfterBookingEnd(now);
        
        if (expiredUnclaimedTickets.isEmpty()) {
            logger.debug("No unclaimed tickets found past booking end time");
            return;
        }
        
        logger.info("Found {} unclaimed tickets past booking end time", expiredUnclaimedTickets.size());
        
        for (Ticket ticket : expiredUnclaimedTickets) {
            logger.info("Cleaning up unclaimed ticket ID: {} (zone: {}, end time: {})", 
                       ticket.getId(), ticket.getZone(), ticket.getEndBookingTime());
            ticketRepository.delete(ticket);
        }
        
        logger.info("Successfully cleaned up {} unclaimed tickets", expiredUnclaimedTickets.size());
    }
}