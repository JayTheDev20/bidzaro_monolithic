package com.cateringmarketplace.module.chat.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.chat.model.Conversation;
import com.cateringmarketplace.module.chat.model.Conversation.ConversationStatus;
import com.cateringmarketplace.module.chat.model.Conversation.ConversationType;

/**
 * Repository for Conversation entity operations.
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    // =========================================================
    // BASIC
    // =========================================================

    Optional<Conversation> findByConversationId(String conversationId);

    long countByStatus(ConversationStatus status);

    // =========================================================
    // RELATED ENTITY
    // =========================================================

    @Query("{'related_to.entity_type': ?0, 'related_to.entity_id': ?1}")
    Optional<Conversation> findByRelatedEntity(String entityType, String entityId);

    // =========================================================
    // PARTICIPANTS
    // =========================================================

    @Query("{'participants.user_id': ?0, 'status': 'ACTIVE'}")
    Page<Conversation> findByParticipantUserId(String userId, Pageable pageable);

    @Query("{'participants.user_id': ?0, 'conversation_type': ?1, 'status': 'ACTIVE'}")
    Page<Conversation> findByParticipantAndType(
            String userId,
            ConversationType type,
            Pageable pageable
    );

    @Query("{'participants.user_id': {'$all': [?0, ?1]}, 'conversation_type': ?2}")
    Optional<Conversation> findByParticipantsAndType(
            String userId1,
            String userId2,
            ConversationType type
    );
}
