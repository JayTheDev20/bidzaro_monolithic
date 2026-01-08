package com.cateringmarketplace.module.auth.dto.response;

import com.cateringmarketplace.module.auth.model.NotificationPreferences;
import com.cateringmarketplace.module.auth.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Response DTO for user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String userId;
    private String email;
    private String phone;
    private String userType;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profilePictureUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean twoFactorEnabled;
    private String preferredLanguage;
    private String preferredCurrency;
    private String country;
    private String status;
    private NotificationPreferences notificationPreferences;
    private Instant lastLoginAt;
    private Instant createdAt;

    /**
     * Creates a UserResponse from a User entity.
     */
    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .preferredLanguage(user.getPreferredLanguage())
                .preferredCurrency(user.getPreferredCurrency())
                .country(user.getCountry())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .notificationPreferences(user.getNotificationPreferences())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

