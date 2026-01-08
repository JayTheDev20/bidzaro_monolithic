package com.cateringmarketplace.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility class for password operations.
 */
@Component
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Hashes the raw password.
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifies if raw password matches hashed password.
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    /**
     * Hashes a token (for refresh token storage).
     */
    public String hashToken(String token) {
        return passwordEncoder.encode(token);
    }

    /**
     * Encodes the raw password.
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Checks if raw password matches encoded password.
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Validates password strength.
     * Password must be at least 8 characters with:
     * - 1 uppercase letter
     * - 1 lowercase letter
     * - 1 number
     * - 1 special character
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    /**
     * Gets password strength score (0-4).
     */
    public int getPasswordStrength(String password) {
        if (password == null) return 0;

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;

        return Math.min(score, 4);
    }

    /**
     * Gets the password encoder instance.
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}

