package com.cateringmarketplace.module.auth.service;

import com.cateringmarketplace.common.constant.ErrorMessages;
import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.common.exception.UnauthorizedException;
import com.cateringmarketplace.common.util.JwtUtil;
import com.cateringmarketplace.common.util.OTPUtil;
import com.cateringmarketplace.common.util.PasswordUtil;
import com.cateringmarketplace.module.auth.dto.request.*;
import com.cateringmarketplace.module.auth.dto.response.*;
import com.cateringmarketplace.module.auth.model.*;
import com.cateringmarketplace.module.auth.model.OTPVerification.VerificationType;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.auth.repository.*;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.notification.service.TwilioService;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OTPVerificationRepository otpVerificationRepository;
    private final VendorRepository vendorRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;
    private final TwilioService twilioService;
    private final OTPUtil otpUtil;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    // New property: whether to log plain OTPs (dev only). Default false.
    @Value("${app.otp.log-plain:false}")
    private boolean logPlainOtp;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Registers a new user.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Format phone number based on country
        String formattedPhone = formatPhoneByCountry(request.getPhone(), request.getCountry());

        // Check if email or phone already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_EXISTS", "Email is already registered");
        }
        if (userRepository.existsByPhone(formattedPhone)) {
            throw new ConflictException("PHONE_EXISTS", "Phone number is already registered");
        }

        // Determine preferred currency based on country
        String currency = "USD";
        if ("INDIA".equalsIgnoreCase(request.getCountry())) {
            currency = "INR";
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .phone(formattedPhone)
                .passwordHash(passwordUtil.hashPassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .country(request.getCountry())
                .preferredCurrency(currency)
                .userType(parseUserType(request.getUserType()))
                .status(UserStatus.PENDING_VERIFICATION)
                .fcmToken(request.getFcmToken())
                .deviceInfo(request.getDeviceInfo())
                .notificationPreferences(NotificationPreferences.defaults())
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getUserId());

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getUserType().name());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage(), e);
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getUserType().name());
        String refreshToken = createRefreshToken(user, httpRequest);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessTokenExpiration / 1000,
                UserResponse.fromEntity(user)
        );
    }

    /**
     * Authenticates a user and returns tokens.
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for identifier: {}", request.getIdentifier());

        // Find user by email or phone
        Optional<User> userOpt = findUserByIdentifier(request.getIdentifier());

        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Email or phone invalid");
        }

        User user = userOpt.get();

        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("ACCOUNT_LOCKED",
                    "Account is temporarily locked due to multiple failed login attempts. Please check your email to reset your password.");
        }

        // Verify password
        if (!passwordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Password incorrect");
        }

        // Check if account is active
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UnauthorizedException("ACCOUNT_SUSPENDED", "Your account has been suspended");
        }
        if (user.getStatus() == UserStatus.DELETED) {
            throw new UnauthorizedException("ACCOUNT_DELETED", "Account not found");
        }

        // Check Vendor Approval Status
        if (user.getUserType() == UserType.VENDOR) {
            Optional<Vendor> vendorOpt = vendorRepository.findByUserId(user.getUserId());
            if (vendorOpt.isPresent()) {
                Vendor vendor = vendorOpt.get();
                // If vendor profile exists but not approved, block login
                if (vendor.getApprovalStatus() != Vendor.ApprovalStatus.APPROVED) {
                    throw new UnauthorizedException("VENDOR_NOT_APPROVED",
                        "Your vendor account is currently " + vendor.getApprovalStatus() + ". Please wait for admin approval.");
                }
            } else {
                // Vendor profile does NOT exist yet.
                // Allow login so they can create their profile.
            }
        }

        // Reset failed login attempts and update last login
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(Instant.now());
        if (request.getFcmToken() != null) {
            user.setFcmToken(request.getFcmToken());
        }
        if (request.getDeviceInfo() != null) {
            user.setDeviceInfo(request.getDeviceInfo());
        }
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getUserType().name());
        String refreshToken = createRefreshToken(user, httpRequest);

        log.info("User logged in successfully: {}", user.getUserId());

        return AuthResponse.of(
                accessToken,
                refreshToken,
                accessTokenExpiration / 1000,
                UserResponse.fromEntity(user)
        );
    }

    /**
     * Refreshes access token using refresh token.
     */
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        log.debug("Refreshing token");

        String tokenHash = passwordUtil.hashToken(request.getRefreshToken());
        RefreshToken storedToken = refreshTokenRepository
                .findValidTokenByHash(tokenHash, Instant.now())
                .orElseThrow(() -> new UnauthorizedException("INVALID_TOKEN", "Invalid or expired refresh token"));

        // Mark old token as replaced
        storedToken.revoke("Token refreshed");
        refreshTokenRepository.save(storedToken);

        // Get user
        User user = userRepository.findByUserId(storedToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getUserType().name());
        String newRefreshToken = createRefreshToken(user, httpRequest);

        // Update replaced by
        storedToken.setReplacedBy(newRefreshToken);
        refreshTokenRepository.save(storedToken);

        return TokenResponse.of(newAccessToken, newRefreshToken, accessTokenExpiration / 1000);
    }

    /**
     * Logs out the user by revoking refresh token.
     */
    @Transactional
    public void logout(String refreshToken, String userId) {
        log.info("Logging out user: {}", userId);

        if (refreshToken != null) {
            String tokenHash = passwordUtil.hashToken(refreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash)
                    .ifPresent(token -> {
                        token.revoke("User logout");
                        refreshTokenRepository.save(token);
                    });
        }
    }

    /**
     * Logs out user from all devices by revoking all refresh tokens.
     */
    @Transactional
    public void logoutAll(String userId) {
        log.info("Logging out user from all devices: {}", userId);

        List<RefreshToken> tokens = refreshTokenRepository.findTokensToRevokeByUserId(userId);
        tokens.forEach(token -> token.revoke("Logout from all devices"));
        refreshTokenRepository.saveAll(tokens);
    }

    /**
     * Sends OTP for verification.
     */
    @Transactional
    public OTPResponse sendOTP(SendOTPRequest request) {
        log.info("Sending OTP to: {}", request.getIdentifier());

        VerificationType type = VerificationType.valueOf(request.getType().toUpperCase());
        String otp = otpUtil.generateOTP();

        // Delete any existing OTP for this identifier and type
        otpVerificationRepository.deleteByIdentifierAndVerificationType(request.getIdentifier(), type);

        // Create new OTP verification
        OTPVerification verification = OTPVerification.builder()
                .identifier(request.getIdentifier())
                .verificationType(type)
                .otpHash(passwordUtil.hashPassword(otp))
                .expiresAt(Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES))
                .build();

        verification = otpVerificationRepository.save(verification);

        // If Twilio not configured and logging enabled, print OTP to logs for dev/testing
        try {
            if (!twilioService.isConfigured() && !request.getIdentifier().contains("@") && logPlainOtp) {
                log.warn("[DEV-OTP] Plain OTP for {}: {} (expires in {} minutes). Enable app.otp.log-plain=false in production.",
                        request.getIdentifier(), otp, otpExpiryMinutes);
            }
        } catch (Exception e) {
            log.debug("Failed to do dev OTP logging: {}", e.getMessage());
        }

        // Send OTP via appropriate channel
        try {
            String purpose = switch (type) {
                case EMAIL -> "Email Verification";
                case PHONE -> "Phone Verification";
                case PASSWORD_RESET -> "Password Reset";
                case TWO_FACTOR -> "Two-Factor Authentication";
                case BUSINESS_EMAIL -> "Business Email Verification";
                default -> "Verification";
            };

            if (request.getIdentifier().contains("@")) {
                // Send via email
                emailService.sendOTPEmail(request.getIdentifier(), otp, purpose);
                log.info("OTP sent via email to: {}", request.getIdentifier());
            } else {
                // Determine channel preference: WHATSAPP, SMS, or AUTO (default)
                String channelPref = request.getChannel() != null ? request.getChannel().toUpperCase() : "AUTO";

                switch (channelPref) {
                    case "WHATSAPP":
                        // Send only via WhatsApp
                        try {
                            if (twilioService.isConfigured() && twilioService.sendOTPWhatsApp(request.getIdentifier(), otp, purpose)) {
                                log.info("OTP sent via WhatsApp to: {}", request.getIdentifier());
                            } else {
                                log.warn("Failed to send WhatsApp OTP to: {}", request.getIdentifier());
                            }
                        } catch (Exception e) {
                            log.error("Error sending WhatsApp OTP to {}: {}", request.getIdentifier(), e.getMessage(), e);
                        }
                        break;

                    case "SMS":
                        // Send only via SMS
                        try {
                            if (twilioService.isConfigured() && twilioService.sendOTPSMS(request.getIdentifier(), otp, purpose)) {
                                log.info("OTP sent via SMS to: {}", request.getIdentifier());
                            } else {
                                log.warn("Failed to send SMS OTP to: {}", request.getIdentifier());
                            }
                        } catch (Exception e) {
                            log.error("Error sending SMS OTP to {}: {}", request.getIdentifier(), e.getMessage(), e);
                        }
                        break;

                    default:
                        // AUTO: Try WhatsApp first, then fallback to SMS
                        boolean sent = false;

                        if (twilioService.isConfigured()) {
                            try {
                                sent = twilioService.sendOTPWhatsApp(request.getIdentifier(), otp, purpose);
                                if (sent) {
                                    log.info("OTP sent via WhatsApp to: {}", request.getIdentifier());
                                } else {
                                    log.warn("WhatsApp OTP failed for {}. Falling back to SMS.", request.getIdentifier());
                                    sent = twilioService.sendOTPSMS(request.getIdentifier(), otp, purpose);
                                    if (sent) {
                                        log.info("OTP sent via SMS to: {}", request.getIdentifier());
                                    } else {
                                        log.warn("Failed to send OTP via SMS to: {}. Twilio may not be configured.", request.getIdentifier());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Error sending OTP via Twilio to {}: {}", request.getIdentifier(), e.getMessage(), e);
                                // Attempt SMS fallback when WhatsApp attempt throws
                                try {
                                    sent = twilioService.sendOTPSMS(request.getIdentifier(), otp, purpose);
                                    if (sent) {
                                        log.info("OTP sent via SMS to: {}", request.getIdentifier());
                                    } else {
                                        log.warn("Failed to send OTP via SMS to: {} after WhatsApp error.", request.getIdentifier());
                                    }
                                } catch (Exception ex) {
                                    log.error("Error sending OTP via SMS to {}: {}", request.getIdentifier(), ex.getMessage(), ex);
                                }
                            }
                        } else {
                            log.warn("Twilio not configured - cannot send WhatsApp/SMS OTP to: {}", request.getIdentifier());
                        }
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage(), e);
        }

        log.info("OTP generated for verification ID: {}", verification.getVerificationId());

        return OTPResponse.sent(verification.getVerificationId(), otpExpiryMinutes * 60);
    }

    /**
     * Verifies OTP.
     */
    @Transactional
    public OTPResponse verifyOTP(VerifyOTPRequest request) {
        log.info("Verifying OTP for: {}", request.getIdentifier());

        VerificationType type = VerificationType.valueOf(request.getType().toUpperCase());

        OTPVerification verification = otpVerificationRepository
                .findLatestValidOTP(request.getIdentifier(), type, Instant.now())
                .orElseThrow(() -> new BadRequestException("OTP_NOT_FOUND", "No valid OTP found"));

        if (!verification.isValid()) {
            throw new BadRequestException("OTP_INVALID", "OTP has expired or exceeded maximum attempts");
        }

        verification.incrementAttempts();

        if (!passwordUtil.verifyPassword(request.getOtp(), verification.getOtpHash())) {
            otpVerificationRepository.save(verification);
            throw new BadRequestException("OTP_MISMATCH", "Invalid OTP");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(Instant.now());
        otpVerificationRepository.save(verification);

        // If email/phone verification, update user status
        if (type == VerificationType.EMAIL) {
            userRepository.findByEmail(request.getIdentifier())
                    .ifPresent(user -> {
                        user.setEmailVerified(true);
                        if (user.getPhoneVerified() || user.getPhone() == null) {
                            user.setStatus(UserStatus.ACTIVE);
                        }
                        userRepository.save(user);

                        // Also update vendor registered email verification status if applicable
                        vendorRepository.findByUserId(user.getUserId())
                                .ifPresent(vendor -> {
                                    // We don't have a direct field for registered email verified in Vendor entity
                                    // but we can infer it from the user.
                                    // However, if there was a field like registeredEmailVerified in Vendor, we would update it here.
                                    // Based on Vendor model, there isn't one explicitly named 'registeredEmailVerified' that is persisted
                                    // The VendorResponse maps it from User entity dynamically.
                                    // But if the user wants to update something in vendor collection, let's check if there are any related fields.
                                    // Vendor model has: businessEmailVerified, businessPhoneVerified.
                                    // It does NOT have registeredEmailVerified stored in DB.
                                    // But let's check if we need to sync anything else.
                                });
                    });
        } else if (type == VerificationType.PHONE) {
            userRepository.findByPhone(request.getIdentifier())
                    .ifPresent(user -> {
                        user.setPhoneVerified(true);
                        if (user.getEmailVerified()) {
                            user.setStatus(UserStatus.ACTIVE);
                        }
                        userRepository.save(user);
                    });
        } else if (type == VerificationType.BUSINESS_EMAIL) {
            vendorRepository.findByBusinessEmail(request.getIdentifier().toLowerCase().trim())
                    .ifPresent(vendor -> {
                        vendor.setBusinessEmailVerified(true);
                        vendorRepository.save(vendor);
                    });
        }

        return OTPResponse.verified();
    }

    /**
     * Initiates forgot password flow.
     */
    @Transactional
    public OTPResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password request for: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // Send OTP
        return sendOTP(SendOTPRequest.builder()
                .identifier(user.getEmail())
                .type("PASSWORD_RESET")
                .build());
    }

    /**
     * Resets password using OTP.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for: {}", request.getEmail());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD_MISMATCH", "Passwords do not match");
        }

        // Verify OTP first
        verifyOTP(VerifyOTPRequest.builder()
                .identifier(request.getEmail())
                .otp(request.getOtp())
                .type("PASSWORD_RESET")
                .build());

        // Update password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        user.setPasswordHash(passwordUtil.hashPassword(request.getNewPassword()));
        
        // Unlock account if it was locked
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        
        userRepository.save(user);

        // Revoke all refresh tokens for security
        logoutAll(user.getUserId());

        log.info("Password reset successfully for user: {}", user.getUserId());
    }

    /**
     * Changes password for authenticated user.
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request, String userId) {
        log.info("Changing password for user: {}", userId);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD_MISMATCH", "Passwords do not match");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (!passwordUtil.verifyPassword(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("INVALID_PASSWORD", "Current password is incorrect");
        }

        user.setPasswordHash(passwordUtil.hashPassword(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Gets current user info.
     */
    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
        return UserResponse.fromEntity(user);
    }

    // ==================== HELPER METHODS ====================

    private Optional<User> findUserByIdentifier(String identifier) {
        // Try to find by email first, then by phone
        return userRepository.findByEmail(identifier.toLowerCase())
                .or(() -> userRepository.findByPhone(identifier));
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 3) {
            // Lock account for 30 minutes
            user.setLockedUntil(Instant.now().plus(30, ChronoUnit.MINUTES));
            log.warn("Account locked due to 3 failed login attempts: {}", user.getUserId());
            
            // Send account locked email with reset link
            // We need to generate a token or just send them to the forgot password page
            // Since we use OTP for reset, we can just direct them to the reset page
            String resetLink = frontendUrl + "/forgot-password";
            emailService.sendAccountLockedEmail(user.getEmail(), user.getFirstName(), resetLink);
        }

        userRepository.save(user);
    }

    private String createRefreshToken(User user, HttpServletRequest httpRequest) {
        String rawToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getEmail());
        String tokenHash = passwordUtil.hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getUserId())
                .tokenHash(tokenHash)
                .deviceInfo(user.getDeviceInfo())
                .ipAddress(getClientIp(httpRequest))
                .userAgent(httpRequest != null ? httpRequest.getHeader("User-Agent") : null)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private UserType parseUserType(String userType) {
        if (userType == null || userType.isEmpty()) {
            return UserType.USER;
        }
        try {
            return UserType.valueOf(userType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserType.USER;
        }
    }

    private String formatPhoneByCountry(String phone, String country) {
        if (phone == null || phone.isEmpty()) return phone;
        
        // Remove spaces, dashes, parentheses
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        
        // If already has +, assume it's correct
        if (cleaned.startsWith("+")) return cleaned;
        
        if ("USA".equalsIgnoreCase(country)) {
            // If 10 digits, add +1
            if (cleaned.length() == 10) return "+1" + cleaned;
            // If 11 digits starting with 1, add +
            if (cleaned.length() == 11 && cleaned.startsWith("1")) return "+" + cleaned;
        } else if ("INDIA".equalsIgnoreCase(country)) {
            // If 10 digits, add +91
            if (cleaned.length() == 10) return "+91" + cleaned;
            // If 12 digits starting with 91, add +
            if (cleaned.length() == 12 && cleaned.startsWith("91")) return "+" + cleaned;
        }
        
        // Default fallback: if 10 digits, assume India (+91) as per previous logic, or just return as is
        if (cleaned.length() == 10) return "+91" + cleaned;
        
        return cleaned;
    }
}
