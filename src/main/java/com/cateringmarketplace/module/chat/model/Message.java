package com.cateringmarketplace.module.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Message entity for chat messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("message_id")
    private String messageId;

    @Indexed
    @Field("conversation_id")
    private String conversationId;

    @Field("sender_id")
    private String senderId;

    @Field("sender_type")
    private String senderType;

    private String message;

    @Field("message_type")
    private MessageType messageType;

    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    @Field("read_by")
    @Builder.Default
    private List<ReadReceipt> readBy = new ArrayList<>();

    @Field("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Field("deleted_at")
    private Instant deletedAt;

    @Indexed
    private Instant timestamp;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // Enums
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        @Field("file_name")
        private String fileName;
        @Field("file_url")
        private String fileUrl;
        @Field("file_type")
        private String fileType;
        @Field("file_size")
        private Long fileSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadReceipt {
        @Field("user_id")
        private String userId;
        @Field("read_at")
        private Instant readAt;
    }

    /**
     * Checks if message is read by user.
     */
    public boolean isReadByUser(String userId) {
        return readBy.stream()
                .anyMatch(r -> r.getUserId().equals(userId));
    }

    /**
     * Marks message as read by user.
     */
    public void markAsRead(String userId) {
        if (!isReadByUser(userId)) {
            readBy.add(ReadReceipt.builder()
                    .userId(userId)
                    .readAt(Instant.now())
                    .build());
        }
    }

    /**
     * Soft deletes the message.
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}

