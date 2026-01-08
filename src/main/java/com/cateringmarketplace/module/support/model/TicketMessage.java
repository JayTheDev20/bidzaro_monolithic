package com.cateringmarketplace.module.support.model;
}
    }
        private Long fileSize;
        @Field("file_size")
        private String fileType;
        @Field("file_type")
        private String fileUrl;
        @Field("file_url")
        private String fileName;
        @Field("file_name")
    public static class MessageAttachment {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data

    private Instant createdAt;
    @Field("created_at")
    @CreatedDate

    private Boolean isInternal = false; // Internal notes visible only to support team
    @Builder.Default
    @Field("is_internal")

    private List<MessageAttachment> attachments = new ArrayList<>();
    @Builder.Default

    private String message;

    private String senderName;
    @Field("sender_name")

    private String senderType; // USER, VENDOR, SUPPORT_AGENT, ADMIN, SYSTEM
    @Field("sender_type")

    private String senderId;
    @Field("sender_id")

    private String ticketId;
    @Field("ticket_id")
    @Indexed

    private String ticketMessageId;
    @Field("ticket_message_id")
    @Indexed(unique = true)

    private String id;
    @Id

public class TicketMessage {
@Document(collection = "ticket_messages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
 */
 * TicketMessage entity for support ticket conversations.
/**

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;


