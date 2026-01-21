package com.cateringmarketplace.module.auth.model;

import com.cateringmarketplace.module.auth.model.enums.Gender;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.loyalty.model.LoyaltyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * User entity representing all users in the system (customers, vendors, admins, support).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("user_id")
    @Builder.Default
    private String userId = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String phone;

    @Field("password_hash")
    private String passwordHash;

    @Field("user_type")
    @Builder.Default
    private UserType userType = UserType.USER;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("profile_picture_url")
    private String profilePictureUrl;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Field("email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Field("phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @Field("two_factor_enabled")
    @Builder.Default
    private Boolean twoFactorEnabled = false;

    @Field("two_factor_secret")
    private String twoFactorSecret;

    @Field("last_login_at")
    private Instant lastLoginAt;

    @Field("failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Field("locked_until")
    private Instant lockedUntil;

    @Field("preferred_language")
    @Builder.Default
    private String preferredLanguage = "en";

    @Field("preferred_currency")
    @Builder.Default
    private String preferredCurrency = "USD";

    @Builder.Default
    private String country = "USA";

    @Field("notification_preferences")
    private NotificationPreferences notificationPreferences;

    private LoyaltyInfo loyalty;

    @Field("referred_by")
    private String referredBy;

    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Field("fcm_token")
    private String fcmToken;

    @Field("device_info")
    private String deviceInfo;

    @Field("profile_reminder_count")
    @Builder.Default
    private Integer profileReminderCount = 0;

    @Field("last_reminder_sent_at")
    private Instant lastReminderSentAt;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    /**
     * Gets the full name of the user.
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName);
        }
        if (lastName != null) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(lastName);
        }
        return sb.toString();
    }

    /**
     * Checks if the account is locked.
     */
    public boolean isAccountLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    /**
     * Checks if the user is active.
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user has a specific role.
     */
    public boolean hasRole(UserType role) {
        return this.userType == role;
    }
}
