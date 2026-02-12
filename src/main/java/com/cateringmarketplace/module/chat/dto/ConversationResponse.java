package com.cateringmarketplace.module.chat.dto;

import com.cateringmarketplace.module.chat.model.Conversation;
import com.cateringmarketplace.module.chat.model.Conversation.LastMessage;
import com.cateringmarketplace.module.chat.model.Conversation.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private String conversationId;
    private List<Participant> participants;
    private Participant otherParticipant; // The other user in the chat
    private LastMessage lastMessage;
    private Integer unreadCount; // Unread count for the current user
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public static ConversationResponse fromEntity(Conversation conversation, String currentUserId) {
        if (conversation == null) return null;

        Participant other = conversation.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        Integer unread = conversation.getUnreadCount().getOrDefault(currentUserId, 0);

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .participants(conversation.getParticipants())
                .otherParticipant(other)
                .lastMessage(conversation.getLastMessage())
                .unreadCount(unread)
                .status(conversation.getStatus() != null ? conversation.getStatus().name() : "ACTIVE")
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
