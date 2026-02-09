package com.cateringmarketplace.module.support.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.support.model.Ticket;
import com.cateringmarketplace.module.support.model.Ticket.TicketPriority;
import com.cateringmarketplace.module.support.model.Ticket.TicketStatus;

/**
 * Repository for Ticket entity operations.
 */
@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    // =========================================================
    // FIND BY IDS
    // =========================================================

    Optional<Ticket> findByTicketId(String ticketId);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    // =========================================================
    // USER / AGENT
    // =========================================================

    Page<Ticket> findByCreatedBy(String userId, Pageable pageable);

    Page<Ticket> findByAssignedTo(String agentId, Pageable pageable);

    // =========================================================
    // STATUS / PRIORITY
    // =========================================================

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);

    long countByStatus(TicketStatus status);

    long countByPriority(TicketPriority priority);

    long countByAssignedToAndStatus(String agentId, TicketStatus status);
    
    long countByAssignedToAndStatusIn(String agentId, List<TicketStatus> statuses);

    // =========================================================
    // OPEN / ACTIVE / UNASSIGNED
    // =========================================================

    /**
     * Find all open tickets (not resolved or closed).
     */
    @Query("{'status': {'$nin': ['RESOLVED', 'CLOSED']}}")
    Page<Ticket> findOpenTickets(Pageable pageable);

    /**
     * Find unassigned open tickets.
     */
    @Query("{'status': {'$nin': ['RESOLVED', 'CLOSED']}, 'assigned_to': null}")
    Page<Ticket> findUnassignedTickets(Pageable pageable);

    /**
     * Find active tickets created by a user.
     */
    @Query("{'created_by': ?0, 'status': {'$nin': ['RESOLVED', 'CLOSED']}}")
    List<Ticket> findActiveTicketsByUser(String userId);

    // =========================================================
    // CATEGORY
    // =========================================================

    /**
     * Find tickets by category (only active ones).
     */
    @Query("{'category': ?0, 'status': {'$nin': ['RESOLVED', 'CLOSED']}}")
    Page<Ticket> findByCategory(String category, Pageable pageable);

    /**
     * Find tickets by status list.
     */
    List<Ticket> findByStatusIn(List<TicketStatus> statuses);

    /**
     * Find unassigned tickets by status.
     */
    List<Ticket> findByStatusAndAssignedToIsNull(TicketStatus status);
}
