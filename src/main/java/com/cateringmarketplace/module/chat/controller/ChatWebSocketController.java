package com.cateringmarketplace.module.chat.controller;

import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.chat.dto.ChatMessageDTO;
import com.cateringmarketplace.module.chat.dto.ChatMessageResponse;
import com.cateringmarketplace.module.chat.model.Message;
import com.cateringmarketplace.module.chat.model.Message.Attachment;
import com.cateringmarketplace.module.chat.model.Message.MessageType;
import com.cateringmarketplace.module.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.stream.Collectors;

/**
 * WebSocket controller for real-time chat messaging.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles incoming chat messages via WebSocket.
     * Destination: /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO messageDTO, Principal principal) {
        
        String senderId = messageDTO.getSenderId();
        
        // If senderId is missing, try to get it from Principal
        if (senderId == null && principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                senderId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
            }
        }
        
        if (senderId == null) {
            log.error("Sender ID is missing and could not be resolved from Principal");
            return;
        }

        log.info("WebSocket message received from {} to conversation {}",
                senderId, messageDTO.getConversationId());

        try {
            // Parse message type
            MessageType messageType = MessageType.TEXT;
            if (messageDTO.getMessageType() != null) {
                try {
                    messageType = MessageType.valueOf(messageDTO.getMessageType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    messageType = MessageType.TEXT;
                }
            }

            // Send message via service
            Message message = chatService.sendMessage(
                    messageDTO.getConversationId(),
                    senderId,
                    messageDTO.getMessage(),
                    messageType
            );

            // Add attachments if present
            if (messageDTO.getAttachments() != null && !messageDTO.getAttachments().isEmpty()) {
                message.setAttachments(messageDTO.getAttachments().stream()
                        .map(a -> Attachment.builder()
                                .fileName(a.getFileName())
                                .fileUrl(a.getFileUrl())
                                .fileType(a.getFileType())
                                .fileSize(a.getFileSize())
                                .build())
                        .collect(Collectors.toList()));
            }

            // Build response
            ChatMessageResponse response = ChatMessageResponse.fromEntity(message);

            // Broadcast to conversation topic
            messagingTemplate.convertAndSend(
                    "/topic/conversations." + messageDTO.getConversationId(),
                    response
            );

            log.info("Message {} broadcast to conversation {}", message.getMessageId(), messageDTO.getConversationId());

        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            // Send error to sender
            messagingTemplate.convertAndSendToUser(
                    senderId,
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
            );
        }
    }

    /**
     * Handles typing indicator.
     * Destination: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingIndicator indicator) {
        messagingTemplate.convertAndSend(
                "/topic/conversations." + indicator.getConversationId() + ".typing",
                indicator
        );
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TypingIndicator {
        private String conversationId;
        private String userId;
        private String userName;
        private boolean typing;
    }
}
