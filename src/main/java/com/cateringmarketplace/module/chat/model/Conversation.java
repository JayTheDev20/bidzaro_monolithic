package com.cateringmarketplace.module.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversation entity for chat functionality.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
@CompoundIndex(name = "participants_idx", def = "{'participants.user_id': 1}")
public class Conversation {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("conversation_id")
    private String conversationId;

    @Builder.Default
    private List<Participant> participants = new ArrayList<>();

    @Field("conversation_type")
    private ConversationType conversationType;

    @Field("related_to")
    private RelatedEntity relatedTo;

    @Field("last_message")
    private LastMessage lastMessage;

    @Field("unread_count")
    @Builder.Default
    private Map<String, Integer> unreadCount = new HashMap<>();

    private ConversationStatus status;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // Enums
    public enum ConversationType {
        USER_VENDOR,
        USER_SUPPORT,
        VENDOR_SUPPORT
    }

    public enum ConversationStatus {
        ACTIVE,
        ARCHIVED
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Participant {
        @Field("user_id")
        private String userId;
        @Field("user_type")
        private String userType;
        private String name;
        @Field("profile_picture_url")
        private String profilePictureUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedEntity {
        @Field("entity_type")
        private String entityType;
        @Field("entity_id")
        private String entityId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastMessage {
        private String message;
        @Field("sender_id")
        private String senderId;
        private Instant timestamp;
    }

    /**
     * Checks if user is a participant.
     */
    public boolean isParticipant(String userId) {
        return participants.stream()
                .anyMatch(p -> p.getUserId().equals(userId));
    }

    /**
     * Gets other participant (for 1:1 conversations).
     */
    public Participant getOtherParticipant(String currentUserId) {
        return participants.stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates last message.
     */
    public void updateLastMessage(String message, String senderId) {
        this.lastMessage = LastMessage.builder()
                .message(message)
                .senderId(senderId)
                .timestamp(Instant.now())
                .build();
        this.updatedAt = Instant.now();
    }

    /**
     * Increments unread count for a user.
     */
    public void incrementUnreadCount(String userId) {
        unreadCount.merge(userId, 1, Integer::sum);
    }

    /**
     * Resets unread count for a user.
     */
    public void resetUnreadCount(String userId) {
        unreadCount.put(userId, 0);
    }
}

