package com.cateringmarketplace.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Can be email or phone number
     */
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Device FCM token for push notifications
     */
    private String fcmToken;

    /**
     * Device info for tracking
     */
    private String deviceInfo;

    /**
     * Remember me option for extended session
     */
    private boolean rememberMe;
}

