package com.cateringmarketplace.module.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending chat messages via WebSocket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private String conversationId;
    private String senderId;
    private String senderType;
    private String message;
    private String messageType; // TEXT, IMAGE, FILE, SYSTEM
    private List<AttachmentDTO> attachments;

    // =========================================================
    // INNER DTO : ATTACHMENT
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentDTO {

        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
    }
}
