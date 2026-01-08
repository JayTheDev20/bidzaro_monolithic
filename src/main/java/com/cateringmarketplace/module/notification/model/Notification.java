package com.cateringmarketplace.module.notification.model;

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
 * Notification entity for all notification types.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("notification_id")
    private String notificationId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("user_type")
    private String userType;

    @Field("notification_type")
    private String notificationType;

    private NotificationChannel channel;

    private String title;

    private String message;

    private NotificationData data;

    private NotificationPriority priority;

    @Indexed
    private NotificationStatus status;

    @Field("sent_at")
    private Instant sentAt;

    @Field("delivered_at")
    private Instant deliveredAt;

    @Field("read_at")
    private Instant readAt;

    @Field("failed_reason")
    private String failedReason;

    @Field("retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Field("max_retries")
    @Builder.Default
    private Integer maxRetries = 3;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private Instant createdAt;

    // Enums
    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        WHATSAPP,
        IN_APP
    }

    public enum NotificationPriority {
        HIGH,
        NORMAL,
        LOW
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        READ
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationData {
        @Field("order_id")
        private String orderId;
        @Field("bid_id")
        private String bidId;
        @Field("vendor_id")
        private String vendorId;
        @Field("deep_link")
        private String deepLink;
        @Field("action_buttons")
        @Builder.Default
        private List<ActionButton> actionButtons = new ArrayList<>();
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionButton {
        private String label;
        private String action;
        private String url;
    }

    /**
     * Marks notification as sent.
     */
    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    /**
     * Marks notification as delivered.
     */
    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = Instant.now();
    }

    /**
     * Marks notification as read.
     */
    public void markRead() {
        this.status = NotificationStatus.READ;
        this.readAt = Instant.now();
    }

    /**
     * Marks notification as failed.
     */
    public void markFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failedReason = reason;
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }

    /**
     * Checks if notification can be retried.
     */
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
}

