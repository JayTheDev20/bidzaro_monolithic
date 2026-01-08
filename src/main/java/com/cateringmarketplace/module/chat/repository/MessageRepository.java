package com.cateringmarketplace.module.chat.repository;

import com.cateringmarketplace.module.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Message entity operations.
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    Optional<Message> findByMessageId(String messageId);

    Page<Message> findByConversationIdOrderByTimestampDesc(String conversationId, Pageable pageable);

    @Query("{'conversation_id': ?0, 'is_deleted': false}")
    Page<Message> findActiveMessagesByConversation(String conversationId, Pageable pageable);

    @Query("{'conversation_id': ?0, 'timestamp': {'$gt': ?1}}")
    List<Message> findMessagesAfter(String conversationId, Instant after);

    @Query("{'conversation_id': ?0, 'read_by.user_id': {'$ne': ?1}, 'sender_id': {'$ne': ?1}}")
    List<Message> findUnreadMessages(String conversationId, String userId);

    long countByConversationId(String conversationId);

    void deleteByConversationId(String conversationId);
}

