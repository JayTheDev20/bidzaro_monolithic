package com.cateringmarketplace.module.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document for user notification preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {

    @Builder.Default
    private EmailNotificationSettings emailNotifications = new EmailNotificationSettings();

    @Builder.Default
    private SmsNotificationSettings smsNotifications = new SmsNotificationSettings();

    @Builder.Default
    private PushNotificationSettings pushNotifications = new PushNotificationSettings();

    @Builder.Default
    private WhatsappNotificationSettings whatsappNotifications = new WhatsappNotificationSettings();

    /**
     * Email notification settings.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailNotificationSettings {
        @Builder.Default
        private boolean orderUpdates = true;
        @Builder.Default
        private boolean bidUpdates = true;
        @Builder.Default
        private boolean promotional = false;
        @Builder.Default
        private boolean newsletter = false;
        @Builder.Default
        private boolean paymentReminders = true;
        @Builder.Default
        private boolean securityAlerts = true;
    }

    /**
     * SMS notification settings.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsNotificationSettings {
        @Builder.Default
        private boolean orderUpdates = true;
        @Builder.Default
        private boolean bidUpdates = true;
        @Builder.Default
        private boolean promotional = false;
        @Builder.Default
        private boolean otpAlerts = true;
    }

    /**
     * Push notification settings.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushNotificationSettings {
        @Builder.Default
        private boolean orderUpdates = true;
        @Builder.Default
        private boolean bidUpdates = true;
        @Builder.Default
        private boolean chatMessages = true;
        @Builder.Default
        private boolean promotional = false;
    }

    /**
     * WhatsApp notification settings.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WhatsappNotificationSettings {
        @Builder.Default
        private boolean orderUpdates = true;
        @Builder.Default
        private boolean bidUpdates = true;
        @Builder.Default
        private boolean promotional = false;
    }

    /**
     * Creates default notification preferences.
     */
    public static NotificationPreferences defaults() {
        return NotificationPreferences.builder().build();
    }
}
