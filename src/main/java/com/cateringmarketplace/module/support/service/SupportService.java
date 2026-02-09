package com.cateringmarketplace.module.support.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.chat.model.Conversation;
import com.cateringmarketplace.module.chat.model.Conversation.ConversationType;
import com.cateringmarketplace.module.chat.service.ChatService;
import com.cateringmarketplace.module.support.dto.request.CreateTicketRequest;
import com.cateringmarketplace.module.support.dto.response.TicketResponse;
import com.cateringmarketplace.module.support.model.Ticket;
import com.cateringmarketplace.module.support.model.Ticket.*;
import com.cateringmarketplace.module.support.repository.TicketRepository;

/**
 * Service class for support ticket operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupportService {

    private static final AtomicLong ticketCounter =
            new AtomicLong(System.currentTimeMillis() % 100000);

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ChatService chatService; // Injected ChatService

    // =========================================================
    // CREATE TICKET
    // =========================================================

    /**
     * Creates a new support ticket.
     */
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, String userId) {

        log.info("Creating support ticket for user: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Priority
        TicketPriority priority = TicketPriority.MEDIUM;
        if (request.getPriority() != null) {
            try {
                priority = TicketPriority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        // Ticket number
        String ticketNumber = generateTicketNumber();
        Instant now = Instant.now();

        // SLA
        SLA sla = calculateSLA(priority, now);

        Ticket ticket = Ticket.builder()
                .ticketId(UUID.randomUUID().toString())
                .ticketNumber(ticketNumber)
                .createdBy(userId)
                .createdByType(user.getUserType().name())
                .createdByName(user.getFullName())
                .createdByEmail(user.getEmail())
                .createdByPhone(user.getPhone())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .priority(priority)
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(TicketStatus.OPEN)
                .sla(sla)
                .build();

        // Related entities
        if (request.getOrderId() != null ||
                request.getVendorId() != null ||
                request.getPaymentId() != null) {

            ticket.setRelatedEntities(
                    RelatedEntities.builder()
                            .orderId(request.getOrderId())
                            .vendorId(request.getVendorId())
                            .paymentId(request.getPaymentId())
                            .build()
            );
        }

        // Attachments
        if (request.getAttachmentUrls() != null) {
            for (String url : request.getAttachmentUrls()) {
                ticket.getAttachments().add(
                        TicketAttachment.builder()
                                .fileUrl(url)
                                .uploadedAt(now)
                                .build()
                );
            }
        }

        ticket = ticketRepository.save(ticket);
        log.info("Created ticket: {} ({})", ticket.getTicketId(), ticket.getTicketNumber());

        // Auto-assign ticket
        autoAssignTicket(ticket);

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Automatically assigns ticket to the agent with the least workload.
     */
    private void autoAssignTicket(Ticket ticket) {
        try {
            List<User> agents = userRepository.findByUserType(UserType.SUPPORT_AGENT);
            
            if (agents.isEmpty()) {
                log.warn("No support agents found for auto-assignment.");
                return;
            }

            String bestAgentId = null;
            long minTickets = Long.MAX_VALUE;

            List<TicketStatus> activeStatuses = Arrays.asList(
                    TicketStatus.OPEN, 
                    TicketStatus.ASSIGNED, 
                    TicketStatus.IN_PROGRESS, 
                    TicketStatus.WAITING_FOR_CUSTOMER
            );

            for (User agent : agents) {
                long activeCount = ticketRepository.countByAssignedToAndStatusIn(agent.getUserId(), activeStatuses);
                if (activeCount < minTickets) {
                    minTickets = activeCount;
                    bestAgentId = agent.getUserId();
                }
            }

            if (bestAgentId != null) {
                assignTicketToAgent(ticket, bestAgentId);
                log.info("Auto-assigned ticket {} to agent {}", ticket.getTicketNumber(), bestAgentId);
            }

        } catch (Exception e) {
            log.error("Failed to auto-assign ticket: {}", e.getMessage(), e);
        }
    }

    /**
     * Helper to assign ticket and create chat.
     */
    private void assignTicketToAgent(Ticket ticket, String agentId) {
        ticket.setAssignedTo(agentId);
        ticket.setAssignedAt(Instant.now());
        ticket.setStatus(TicketStatus.ASSIGNED);

        // Create Chat Conversation
        try {
            ConversationType type = "VENDOR".equalsIgnoreCase(ticket.getCreatedByType()) 
                    ? ConversationType.VENDOR_SUPPORT 
                    : ConversationType.USER_SUPPORT;

            Conversation conversation = chatService.getOrCreateConversation(
                    ticket.getCreatedBy(), agentId, type);
            
            ticket.setConversationId(conversation.getConversationId());
            
        } catch (Exception e) {
            log.error("Failed to create support chat for ticket {}: {}", ticket.getTicketNumber(), e.getMessage());
        }

        ticketRepository.save(ticket);
    }

    // =========================================================
    // READ
    // =========================================================

    /**
     * Gets ticket by ID.
     */
    public TicketResponse getTicket(String ticketId, String userId) {

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Access check
        if (!ticket.getCreatedBy().equals(userId) && !isSupport(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot access this ticket");
        }

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Gets tickets for a user.
     */
    public Page<TicketResponse> getUserTickets(String userId, Pageable pageable) {
        return ticketRepository.findByCreatedBy(userId, pageable)
                .map(TicketResponse::fromEntity);
    }

    /**
     * Gets tickets assigned to an agent.
     */
    public Page<TicketResponse> getAgentTickets(String agentId, Pageable pageable) {
        return ticketRepository.findByAssignedTo(agentId, pageable)
                .map(TicketResponse::fromEntity);
    }

    /**
     * Gets open tickets (for support).
     */
    public Page<TicketResponse> getOpenTickets(Pageable pageable) {
        return ticketRepository.findOpenTickets(pageable)
                .map(TicketResponse::fromEntity);
    }

    // =========================================================
    // ASSIGN / STATUS
    // =========================================================

    /**
     * Assigns ticket to an agent.
     */
    @Transactional
    public TicketResponse assignTicket(String ticketId, String agentId, String assignedBy) {

        log.info("Assigning ticket {} to agent {}", ticketId, agentId);

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        assignTicketToAgent(ticket, agentId);

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Updates ticket status.
     */
    @Transactional
    public TicketResponse updateStatus(String ticketId, TicketStatus newStatus, String userId) {

        log.info("Updating ticket {} status to {}", ticketId, newStatus);

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // First response time
        if (ticket.getSla() != null &&
                ticket.getSla().getFirstResponseAt() == null &&
                (newStatus == TicketStatus.IN_PROGRESS ||
                        newStatus == TicketStatus.WAITING_FOR_CUSTOMER)) {

            ticket.getSla().setFirstResponseAt(Instant.now());
        }

        // Resolution handling
        if (newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.CLOSED) {

            if (ticket.getSla() != null) {
                ticket.getSla().setResolvedAt(Instant.now());

                if (ticket.getSla().getResolutionDue() != null &&
                        Instant.now().isAfter(ticket.getSla().getResolutionDue())) {
                    ticket.getSla().setSlaBreached(true);
                }
            }

            if (newStatus == TicketStatus.CLOSED) {
                ticket.setClosedAt(Instant.now());
            }
        }

        ticket.setStatus(newStatus);
        ticket = ticketRepository.save(ticket);

        return TicketResponse.fromEntity(ticket);
    }

    // =========================================================
    // RESOLUTION
    // =========================================================

    /**
     * Resolves a ticket.
     */
    @Transactional
    public TicketResponse resolveTicket(String ticketId,
                                        String resolutionNotes,
                                        String resolvedBy) {

        log.info("Resolving ticket: {}", ticketId);

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setResolution(
                Resolution.builder()
                        .resolutionType("RESOLVED")
                        .resolutionNotes(resolutionNotes)
                        .resolvedBy(resolvedBy)
                        .resolvedAt(Instant.now())
                        .build()
        );

        ticket.setStatus(TicketStatus.RESOLVED);

        if (ticket.getSla() != null) {
            ticket.getSla().setResolvedAt(Instant.now());
        }

        ticket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(ticket);
    }

    // =========================================================
    // CUSTOMER SATISFACTION
    // =========================================================

    /**
     * Adds customer satisfaction rating.
     */
    @Transactional
    public TicketResponse rateTicket(String ticketId,
                                     int rating,
                                     String feedback,
                                     String userId) {

        Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!ticket.getCreatedBy().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "Only the ticket creator can rate");
        }

        if (ticket.getStatus() != TicketStatus.RESOLVED &&
                ticket.getStatus() != TicketStatus.CLOSED) {
            throw new BadRequestException("INVALID_STATUS",
                    "Can only rate resolved tickets");
        }

        ticket.setCustomerSatisfaction(
                CustomerSatisfaction.builder()
                        .rating(rating)
                        .feedback(feedback)
                        .build()
        );

        ticket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(ticket);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private String generateTicketNumber() {
        long counter = ticketCounter.incrementAndGet();
        return String.format("TKT-%d-%05d",
                Instant.now().toEpochMilli() % 10000,
                counter % 100000);
    }

    private SLA calculateSLA(TicketPriority priority, Instant createdAt) {

        int firstResponseHours;
        int resolutionHours;

        switch (priority) {
            case URGENT -> { firstResponseHours = 1; resolutionHours = 4; }
            case HIGH   -> { firstResponseHours = 4; resolutionHours = 24; }
            case MEDIUM -> { firstResponseHours = 8; resolutionHours = 48; }
            default     -> { firstResponseHours = 24; resolutionHours = 72; }
        }

        return SLA.builder()
                .firstResponseDue(createdAt.plus(firstResponseHours, ChronoUnit.HOURS))
                .resolutionDue(createdAt.plus(resolutionHours, ChronoUnit.HOURS))
                .slaBreached(false)
                .build();
    }

    private boolean isSupport(String userId) {
        return userRepository.findByUserId(userId)
                .map(user ->
                        user.getUserType().name().equals("SUPPORT_AGENT") ||
                                user.getUserType().name().equals("ADMIN"))
                .orElse(false);
    }
}
