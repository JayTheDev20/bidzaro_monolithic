package com.cateringmarketplace.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for OTP operations.
 */
@Component
@Slf4j
public class OTPUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.expiry-minutes:10}")
    private int expiryMinutes;

    /**
     * Generates a numeric OTP.
     */
    public String generateNumericOTP() {
        return generateOTP(NUMERIC_CHARS, otpLength);
    }

    /**
     * Generates a default OTP (numeric, configured length).
     */
    public String generateOTP() {
        return generateNumericOTP();
    }

    /**
     * Generates a numeric OTP with custom length.
     */
    public String generateNumericOTP(int length) {
        return generateOTP(NUMERIC_CHARS, length);
    }

    /**
     * Generates an alphanumeric OTP.
     */
    public String generateAlphanumericOTP() {
        return generateOTP(ALPHANUMERIC_CHARS, otpLength);
    }

    /**
     * Generates an alphanumeric OTP with custom length.
     */
    public String generateAlphanumericOTP(int length) {
        return generateOTP(ALPHANUMERIC_CHARS, length);
    }

    /**
     * Generates OTP with specified characters and length.
     */
    private String generateOTP(String characters, int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(characters.charAt(SECURE_RANDOM.nextInt(characters.length())));
        }
        return otp.toString();
    }

    /**
     * Gets the expiry time for OTP.
     */
    public Instant getExpiryTime() {
        return Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES);
    }

    /**
     * Gets the expiry time for OTP with custom minutes.
     */
    public Instant getExpiryTime(int minutes) {
        return Instant.now().plus(minutes, ChronoUnit.MINUTES);
    }

    /**
     * Checks if OTP is expired.
     */
    public boolean isExpired(Instant expiryTime) {
        return Instant.now().isAfter(expiryTime);
    }

    /**
     * Validates OTP format (numeric only).
     */
    public boolean isValidNumericOTP(String otp) {
        if (otp == null || otp.length() != otpLength) {
            return false;
        }
        return otp.matches("\\d{" + otpLength + "}");
    }

    /**
     * Validates OTP format with custom length.
     */
    public boolean isValidNumericOTP(String otp, int length) {
        if (otp == null || otp.length() != length) {
            return false;
        }
        return otp.matches("\\d{" + length + "}");
    }

    /**
     * Generates a verification token for email verification.
     */
    public String generateVerificationToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generates a password reset token.
     */
    public String generatePasswordResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Gets OTP expiry in minutes.
     */
    public int getExpiryMinutes() {
        return expiryMinutes;
    }
}

