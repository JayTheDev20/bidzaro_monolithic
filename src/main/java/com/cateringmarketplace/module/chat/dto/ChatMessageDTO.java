package com.cateringmarketplace.module.chat.dto;
}
    }
        private Long fileSize;
        private String fileType;
        private String fileUrl;
        private String fileName;
    public static class AttachmentDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data

    private List<AttachmentDTO> attachments;
    private String messageType; // TEXT, IMAGE, FILE, SYSTEM
    private String message;
    private String senderType;
    private String senderId;
    private String conversationId;

public class ChatMessageDTO {
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
 */
 * DTO for sending chat messages via WebSocket.
/**

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;


