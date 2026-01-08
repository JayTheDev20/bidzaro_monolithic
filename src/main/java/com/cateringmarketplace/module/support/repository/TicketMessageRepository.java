package com.cateringmarketplace.module.support.repository;

import com.cateringmarketplace.module.support.model.TicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TicketMessage entity.
 */
@Repository
public interface TicketMessageRepository extends MongoRepository<TicketMessage, String> {

    Optional<TicketMessage> findByTicketMessageId(String ticketMessageId);

    Page<TicketMessage> findByTicketIdOrderByCreatedAtAsc(String ticketId, Pageable pageable);

    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(String ticketId);

    @Query("{'ticket_id': ?0, 'is_internal': false}")
    Page<TicketMessage> findPublicMessagesByTicketId(String ticketId, Pageable pageable);

    long countByTicketId(String ticketId);

    void deleteByTicketId(String ticketId);
}

