package com.cateringmarketplace.scheduler;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.module.support.model.Ticket;
import com.cateringmarketplace.module.support.model.Ticket.TicketPriority;
import com.cateringmarketplace.module.support.model.Ticket.TicketStatus;
import com.cateringmarketplace.module.support.repository.TicketRepository;

/**
 * Scheduler for SLA monitoring and ticket escalation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SLAMonitorScheduler {

    private final TicketRepository ticketRepository;

    // =========================================================
    // SLA BREACH CHECK
    // =========================================================

    /**
     * Checks for SLA breaches every 15 minutes.
     */
    @Scheduled(fixedRate = 900000) // 15 minutes
    public void checkSLABreaches() {

        log.info("Running SLA breach check");

        Instant now = Instant.now();
        int breachedCount = 0;

        // Find open tickets past their SLA
        List<Ticket> openTickets = ticketRepository.findByStatusIn(
                List.of(
                        TicketStatus.OPEN,
                        TicketStatus.ASSIGNED,
                        TicketStatus.IN_PROGRESS
                )
        );

        for (Ticket ticket : openTickets) {

            if (ticket.getSla() == null) continue;

            boolean breached = false;

            // Check first response SLA
            if (ticket.getSla().getFirstResponseDue() != null &&
                    ticket.getSla().getFirstResponseAt() == null &&
                    now.isAfter(ticket.getSla().getFirstResponseDue())) {

                breached = true;
            }

            // Check resolution SLA
            if (ticket.getSla().getResolutionDue() != null &&
                    ticket.getSla().getResolvedAt() == null &&
                    now.isAfter(ticket.getSla().getResolutionDue())) {

                breached = true;
            }

            if (breached && !Boolean.TRUE.equals(ticket.getSla().getSlaBreached())) {
                ticket.getSla().setSlaBreached(true);
                ticketRepository.save(ticket);
                breachedCount++;
                log.warn("SLA breached for ticket: {}", ticket.getTicketNumber());
            }
        }

        log.info("SLA breach check completed. Breached tickets: {}", breachedCount);
    }

    // =========================================================
    // AUTO ESCALATION
    // =========================================================

    /**
     * Runs every hour.
     * Auto-escalates high priority tickets that are unassigned for too long.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void autoEscalateTickets() {

        log.info("Running ticket auto-escalation");

        Instant now = Instant.now();
        int escalatedCount = 0;

        // Find unassigned open tickets
        List<Ticket> tickets =
                ticketRepository.findByStatusAndAssignedToIsNull(TicketStatus.OPEN);

        for (Ticket ticket : tickets) {

            long hoursOpen =
                    java.time.Duration
                            .between(ticket.getCreatedAt(), now)
                            .toHours();

            boolean shouldEscalate = false;

            if (ticket.getPriority() == TicketPriority.URGENT && hoursOpen >= 1) {
                shouldEscalate = true;
            } else if (ticket.getPriority() == TicketPriority.HIGH && hoursOpen >= 4) {
                shouldEscalate = true;
            } else if (ticket.getPriority() == TicketPriority.MEDIUM && hoursOpen >= 24) {
                shouldEscalate = true;
            }

            if (shouldEscalate &&
                    ticket.getStatus() != TicketStatus.ESCALATED) {

                ticket.setStatus(TicketStatus.ESCALATED);
                ticketRepository.save(ticket);
                escalatedCount++;

                log.warn(
                        "Auto-escalated ticket: {} (priority: {}, hours open: {})",
                        ticket.getTicketNumber(),
                        ticket.getPriority(),
                        hoursOpen
                );
            }
        }

        log.info("Auto-escalation completed. Escalated tickets: {}", escalatedCount);
    }
}
