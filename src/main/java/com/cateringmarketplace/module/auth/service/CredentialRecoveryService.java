package com.cateringmarketplace.module.auth.service;

import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.notification.service.TwilioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for credential recovery (forgot email/phone).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CredentialRecoveryService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TwilioService twilioService;

    /**
     * Recovers email by phone number - sends email to WhatsApp.
     */
    public Map<String, String> recoverEmailByPhone(String phone) {
        log.info("Recovering email for phone: {}", phone);

        // Find user by phone
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this phone number"));

        // Send email to WhatsApp
        String message = String.format(
            "🔐 Account Recovery\n\n" +
            "Your registered email is: %s\n\n" +
            "Use this email to login to your account.\n\n" +
            "If you didn't request this, please contact support.",
            user.getEmail()
        );

        // Try WhatsApp first, fallback to SMS
        boolean sent = twilioService.sendWhatsApp(phone, message);
        if (!sent) {
            log.warn("WhatsApp failed, falling back to SMS for phone: {}", phone);
            twilioService.sendSMS(phone, "Your registered email is: " + user.getEmail());
        }
        log.info("Email credential sent to phone: {}", phone);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Your email has been sent to your WhatsApp");
        response.put("phone", maskPhone(phone));
        return response;
    }

    /**
     * Recovers phone number by email - sends phone to email.
     */
    public Map<String, String> recoverPhoneByEmail(String email) {
        log.info("Recovering phone for email: {}", email);

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        // Send phone to email
        String subject = "Account Recovery - Your Phone Number";
        String body = String.format(
            "<html><body>" +
            "<h2>Account Recovery</h2>" +
            "<p>Your registered phone number is: <strong>%s</strong></p>" +
            "<p>Use this phone number to login to your account.</p>" +
            "<br>" +
            "<p>If you didn't request this, please contact support immediately.</p>" +
            "<p>Best regards,<br>Bidzaro Team</p>" +
            "</body></html>",
            user.getPhone()
        );

        emailService.sendSimpleEmail(email, subject, body, user.getUserType().name());
        log.info("Phone number sent to email: {}", email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Your phone number has been sent to your email");
        response.put("email", maskEmail(email));
        return response;
    }

    /**
     * Masks phone number for privacy.
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return phone.substring(0, phone.length() - 4).replaceAll("\\d", "*") +
               phone.substring(phone.length() - 4);
    }

    /**
     * Masks email for privacy.
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****@****.***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return "**@" + domain;
        }

        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + domain;
    }
}

