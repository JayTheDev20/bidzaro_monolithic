package com.cateringmarketplace.module.auth.model;

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
import java.util.UUID;

/**
 * OTP verification entity for email/phone verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "otp_verifications")
public class OTPVerification {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("verification_id")
    @Builder.Default
    private String verificationId = UUID.randomUUID().toString();

    @Indexed
    @Field("user_id")
    private String userId;

    /**
     * The identifier being verified (email or phone number)
     */
    @Indexed
    private String identifier;

    /**
     * Type of verification: EMAIL, PHONE, PASSWORD_RESET, TWO_FACTOR
     */
    @Field("verification_type")
    private VerificationType verificationType;

    /**
     * The OTP code (hashed for security)
     */
    @Field("otp_hash")
    private String otpHash;

    /**
     * Number of verification attempts
     */
    @Builder.Default
    private Integer attempts = 0;

    /**
     * Maximum allowed attempts
     */
    @Field("max_attempts")
    @Builder.Default
    private Integer maxAttempts = 3;

    /**
     * Whether OTP has been verified
     */
    @Builder.Default
    private Boolean verified = false;

    /**
     * When the OTP was verified
     */
    @Field("verified_at")
    private Instant verifiedAt;

    /**
     * When the OTP expires
     */
    @Indexed
    @Field("expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    /**
     * Verification type enum.
     */
    public enum VerificationType {
        EMAIL,
        PHONE,
        PASSWORD_RESET,
        TWO_FACTOR,
        LOGIN,
        BUSINESS_EMAIL
    }

    /**
     * Checks if the OTP has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if maximum attempts have been exceeded.
     */
    public boolean isMaxAttemptsExceeded() {
        return attempts >= maxAttempts;
    }

    /**
     * Checks if the OTP is still valid for verification.
     */
    public boolean isValid() {
        return !verified && !isExpired() && !isMaxAttemptsExceeded();
    }

    /**
     * Increments the attempt count.
     */
    public void incrementAttempts() {
        this.attempts++;
    }
}
