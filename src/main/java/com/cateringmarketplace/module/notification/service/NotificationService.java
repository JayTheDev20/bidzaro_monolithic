package com.cateringmarketplace.module.notification.service;

import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.model.Notification;
import com.cateringmarketplace.module.notification.model.Notification.*;
import com.cateringmarketplace.module.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for notification operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TwilioService twilioService;

    /**
     * Sends a notification to a user.
     */
    @Async
    @Transactional
    public void sendNotification(String userId, String title, String message,
                                  String notificationType, NotificationChannel channel,
                                  Map<String, Object> data) {
        log.info("Sending {} notification to user: {}", channel, userId);

        try {
            Optional<User> userOpt = userRepository.findByUserId(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found for notification: {}", userId);
                return;
            }

            User user = userOpt.get();

            // Check user preferences
            if (!shouldSendNotification(user, notificationType, channel)) {
                log.debug("User has disabled {} notifications for {}", channel, notificationType);
                return;
            }

            // Create notification record
            Notification notification = Notification.builder()
                    .notificationId(UUID.randomUUID().toString())
                    .userId(userId)
                    .notificationType(notificationType)
                    .channel(channel)
                    .title(title)
                    .message(message)
                    .priority(NotificationPriority.NORMAL)
                    .status(NotificationStatus.PENDING)
                    .build();

            if (data != null) {
                notification.setData(NotificationData.builder()
                        .orderId(data.get("orderId") != null ? data.get("orderId").toString() : null)
                        .deepLink(data.get("deepLink") != null ? data.get("deepLink").toString() : null)
                        .build());
            }

            notification = notificationRepository.save(notification);

            // Send through appropriate channel
            boolean sent = false;
            switch (channel) {
                case EMAIL:
                    sendEmail(user.getEmail(), title, message, user.getUserType().name());
                    sent = true; // EmailService is async void, assume sent for now or track separately
                    break;
                case SMS:
                    sent = sendSMS(user.getPhone(), message);
                    break;
                case PUSH:
                    sent = sendPushNotification(user.getFcmToken(), title, message, data);
                    break;
                case IN_APP:
                    sent = true; // Already saved
                    break;
                case WHATSAPP:
                    sent = sendWhatsApp(user.getPhone(), message);
                    break;
            }

            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(Instant.now());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setFailedReason("Failed to send notification");
            }

            notificationRepository.save(notification);
            log.info("Notification {} status: {}", notification.getNotificationId(), notification.getStatus());

        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a high-priority notification.
     */
    public void sendUrgentNotification(String userId, String title, String message,
                                        String notificationType, Map<String, Object> data) {
        // Send via all channels for urgent notifications
        sendNotification(userId, title, message, notificationType, NotificationChannel.PUSH, data);
        sendNotification(userId, title, message, notificationType, NotificationChannel.EMAIL, data);
        sendNotification(userId, title, message, notificationType, NotificationChannel.SMS, data);
    }

    /**
     * Gets notifications for a user.
     */
    public Page<Notification> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Gets unread notifications for a user.
     */
    public Page<Notification> getUnreadNotifications(String userId, Pageable pageable) {
        return notificationRepository.findUnreadByUserId(userId, pageable);
    }

    /**
     * Gets unread count for a user.
     */
    public long getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Marks a notification as read.
     */
    @Transactional
    public void markAsRead(String notificationId) {
        notificationRepository.findByNotificationId(notificationId)
                .ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.READ);
                    notification.setReadAt(Instant.now());
                    notificationRepository.save(notification);
                });
    }

    /**
     * Marks all notifications as read for a user.
     */
    @Transactional
    public void markAllAsRead(String userId) {
        Page<Notification> unread = notificationRepository.findUnreadByUserId(userId, Pageable.unpaged());
        unread.forEach(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(Instant.now());
            notificationRepository.save(notification);
        });
    }

    // ==================== HELPER METHODS ====================

    private boolean shouldSendNotification(User user, String notificationType, NotificationChannel channel) {
        if (user.getNotificationPreferences() == null) return true;

        // Check channel-specific preferences
        // This is a simplified check - expand based on notification type
        return true;
    }

    private void sendEmail(String email, String subject, String body, String role) {
        try {
            emailService.sendNotificationEmail(email, subject, body, role);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", email, e.getMessage(), e);
        }
    }

    private boolean sendSMS(String phone, String message) {
        try {
            return twilioService.sendSMS(phone, message);
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    private boolean sendPushNotification(String fcmToken, String title, String body, Map<String, Object> data) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.debug("No FCM token available for push notification");
            return false;
        }
        // TODO: Implement Firebase push notification
        log.debug("Sending push notification with title: {}", title);
        return true;
    }

    private boolean sendWhatsApp(String phone, String message) {
        try {
            return twilioService.sendWhatsApp(phone, message);
        } catch (Exception e) {
            log.error("Error sending WhatsApp to {}: {}", phone, e.getMessage(), e);
            return false;
        }
    }
}
