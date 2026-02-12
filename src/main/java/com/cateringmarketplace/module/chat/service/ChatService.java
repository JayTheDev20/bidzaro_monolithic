package com.cateringmarketplace.module.chat.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.chat.dto.CreateConversationRequest;
import com.cateringmarketplace.module.chat.model.Conversation;
import com.cateringmarketplace.module.chat.model.Conversation.*;
import com.cateringmarketplace.module.chat.model.Message;
import com.cateringmarketplace.module.chat.model.Message.MessageType;
import com.cateringmarketplace.module.chat.model.Message.ReadReceipt;
import com.cateringmarketplace.module.chat.repository.ConversationRepository;
import com.cateringmarketplace.module.chat.repository.MessageRepository;
import com.cateringmarketplace.module.notification.service.FirebaseService;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for chat operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository; // Injected
    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseService firebaseService;

    /**
     * Gets or creates a conversation based on request.
     */
    @Transactional
    public Conversation getOrCreateConversation(String currentUserId, CreateConversationRequest request) {
        String otherUserId = request.getOtherUserId();
        
        // If otherUserId is missing but vendorId is present, resolve user from vendor
        if ((otherUserId == null || otherUserId.isEmpty()) && request.getVendorId() != null) {
            Vendor vendor = vendorRepository.findByVendorId(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
            otherUserId = vendor.getUserId();
        }

        if (otherUserId == null || otherUserId.isEmpty()) {
            throw new BadRequestException("Either otherUserId or vendorId must be provided");
        }

        ConversationType type = ConversationType.valueOf(request.getType().toUpperCase());
        return getOrCreateConversation(currentUserId, otherUserId, type);
    }

    /**
     * Gets or creates a conversation between two users.
     */
    @Transactional
    public Conversation getOrCreateConversation(String userId1, String userId2, ConversationType type) {
        log.info("Getting or creating conversation between {} and {}", userId1, userId2);

        // Check if conversation already exists
        Optional<Conversation> existing = conversationRepository.findByParticipantsAndType(userId1, userId2, type);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Get user details
        User user1 = userRepository.findByUserId(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId1));
        User user2 = userRepository.findByUserId(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId2));

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .conversationId(UUID.randomUUID().toString())
                .conversationType(type)
                .status(ConversationStatus.ACTIVE)
                .build();

        conversation.getParticipants().add(Participant.builder()
                .userId(user1.getUserId())
                .userType(user1.getUserType().name())
                .name(user1.getFullName())
                .profilePictureUrl(user1.getProfilePictureUrl())
                .build());

        conversation.getParticipants().add(Participant.builder()
                .userId(user2.getUserId())
                .userType(user2.getUserType().name())
                .name(user2.getFullName())
                .profilePictureUrl(user2.getProfilePictureUrl())
                .build());

        conversation.getUnreadCount().put(userId1, 0);
        conversation.getUnreadCount().put(userId2, 0);

        conversation = conversationRepository.save(conversation);
        log.info("Created conversation: {}", conversation.getConversationId());

        return conversation;
    }

    /**
     * Gets conversations for a user.
     */
    public Page<Conversation> getUserConversations(String userId, Pageable pageable) {
        return conversationRepository.findByParticipantUserId(userId, pageable);
    }

    /**
     * Gets a conversation by ID.
     */
    public Conversation getConversation(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId));

        if (!isParticipant) {
            throw new ForbiddenException("FORBIDDEN", "You are not a participant in this conversation");
        }

        return conversation;
    }

    /**
     * Sends a message in a conversation.
     */
    @Transactional
    public Message sendMessage(String conversationId, String senderId, String content, MessageType messageType) {
        log.info("Sending message in conversation: {} from: {}", conversationId, senderId);

        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Verify sender is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(senderId));

        if (!isParticipant) {
            throw new ForbiddenException("FORBIDDEN", "You are not a participant in this conversation");
        }

        // Get sender info
        User sender = userRepository.findByUserId(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create message
        Message message = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .senderId(senderId)
                .senderType(sender.getUserType().name())
                .message(content)
                .messageType(messageType != null ? messageType : MessageType.TEXT)
                .timestamp(Instant.now())
                .build();

        message = messageRepository.save(message);

        // Update conversation's last message
        conversation.setLastMessage(LastMessage.builder()
                .message(content)
                .senderId(senderId)
                .timestamp(Instant.now())
                .build());

        // Increment unread count for other participants and send Push Notification
        for (Participant p : conversation.getParticipants()) {
            if (!p.getUserId().equals(senderId)) {
                conversation.getUnreadCount().merge(p.getUserId(), 1, Integer::sum);
                
                // Send Push Notification
                userRepository.findByUserId(p.getUserId()).ifPresent(recipient -> {
                    if (recipient.getFcmToken() != null) {
                        firebaseService.sendPushNotification(
                                recipient.getFcmToken(),
                                "New Message from " + sender.getFullName(),
                                content,
                                conversationId
                        );
                    }
                });
            }
        }

        conversationRepository.save(conversation);

        log.info("Message sent: {}", message.getMessageId());

        return message;
    }

    /**
     * Gets messages for a conversation.
     */
    public Page<Message> getMessages(String conversationId, String userId, Pageable pageable) {
        // Verify access
        getConversation(conversationId, userId);

        return messageRepository.findActiveMessagesByConversation(conversationId, pageable);
    }

    /**
     * Marks messages as read.
     */
    @Transactional
    public void markAsRead(String conversationId, String userId) {
        log.info("Marking messages as read in: {} for user: {}", conversationId, userId);

        List<Message> unread = messageRepository.findUnreadMessages(conversationId, userId);

        if (unread.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        for (Message message : unread) {
            message.getReadBy().add(ReadReceipt.builder()
                    .userId(userId)
                    .readAt(now)
                    .build());
            messageRepository.save(message);
        }

        // Reset unread count
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElse(null);

        if (conversation != null) {
            conversation.getUnreadCount().put(userId, 0);
            conversationRepository.save(conversation);
        }

        // Broadcast Read Receipt
        messagingTemplate.convertAndSend(
                "/topic/conversations." + conversationId + ".read",
                Map.of(
                        "conversationId", conversationId,
                        "userId", userId,
                        "readAt", now
                )
        );
    }

    /**
     * Deletes a message (soft delete).
     */
    @Transactional
    public void deleteMessage(String messageId, String userId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSenderId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You can only delete your own messages");
        }

        message.setIsDeleted(true);
        message.setDeletedAt(Instant.now());
        messageRepository.save(message);
    }
}
