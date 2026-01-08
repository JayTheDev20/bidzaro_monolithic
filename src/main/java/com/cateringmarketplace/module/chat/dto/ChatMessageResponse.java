package com.cateringmarketplace.module.chat.dto;

import com.cateringmarketplace.module.chat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for outgoing chat messages via WebSocket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private String messageId;
    private String conversationId;
    private String senderId;
    private String senderType;
    private String senderName;
    private String message;
    private String messageType;
    private List<AttachmentResponse> attachments;
    private Instant timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentResponse {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
    }

    public static ChatMessageResponse fromEntity(Message message) {
        if (message == null) return null;

        return ChatMessageResponse.builder()
                .messageId(message.getMessageId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .message(message.getMessage())
                .messageType(message.getMessageType() != null ? message.getMessageType().name() : "TEXT")
                .attachments(message.getAttachments() != null ? message.getAttachments().stream()
                        .map(a -> AttachmentResponse.builder()
                                .fileName(a.getFileName())
                                .fileUrl(a.getFileUrl())
                                .fileType(a.getFileType())
                                .fileSize(a.getFileSize())
                                .build())
                        .collect(Collectors.toList()) : null)
                .timestamp(message.getTimestamp())
                .build();
    }
}

