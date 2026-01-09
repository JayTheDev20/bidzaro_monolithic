package com.cateringmarketplace.module.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending SMS and WhatsApp messages via Twilio.
 */
@Service
@Slf4j
public class TwilioService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    @Value("${twilio.whatsapp-number:}")
    private String whatsappNumber;

    // Flag set after init to indicate Twilio is usable
    private boolean twilioReady = false;

    @PostConstruct
    public void init() {
        // Normalize config values and perform basic validation
        if (whatsappNumber != null && whatsappNumber.startsWith("whatsapp:")) {
            whatsappNumber = whatsappNumber.substring("whatsapp:".length());
        }
        if (fromPhoneNumber != null && fromPhoneNumber.startsWith("whatsapp:")) {
            fromPhoneNumber = fromPhoneNumber.substring("whatsapp:".length());
        }

        if (accountSid != null && !accountSid.isEmpty() &&
            !accountSid.startsWith("your_") && authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                // Basic checks for from numbers
                if (fromPhoneNumber == null || fromPhoneNumber.isEmpty() || fromPhoneNumber.startsWith("your_")) {
                    log.warn("Twilio initialized but 'twilio.phone-number' is not set or uses a placeholder. SMS may fail.");
                }
                if (whatsappNumber == null || whatsappNumber.isEmpty() || whatsappNumber.startsWith("your_")) {
                    log.warn("Twilio initialized but 'twilio.whatsapp-number' is not set or uses a placeholder. WhatsApp may fail.");
                }

                twilioReady = true;
                log.info("Twilio service initialized successfully");
            } catch (Exception e) {
                twilioReady = false;
                log.warn("Failed to initialize Twilio: {}", e.getMessage());
            }
        } else {
            twilioReady = false;
            log.warn("Twilio credentials not configured - SMS/WhatsApp will be disabled");
        }
    }

    /**
     * Sends an SMS message to a phone number.
     *
     * @param to Phone number in E.164 format (e.g., +919640206605)
     * @param message The message content
     * @return true if sent successfully, false otherwise
     */
    public boolean sendSMS(String to, String message) {
        if (!isConfigured()) {
            log.debug("Twilio not configured - SMS not sent to: {}", to);
            return false;
        }

        if (fromPhoneNumber == null || fromPhoneNumber.isEmpty()) {
            log.error("Twilio 'from' phone number is not configured. Set 'twilio.phone-number' in config.");
            return false;
        }

        try {
            // Ensure phone number is in E.164 format
            String formattedPhone = formatPhoneNumber(to);

            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(fromPhoneNumber),
                    message
            ).create();

            log.info("SMS sent successfully. SID: {}, To: {}", twilioMessage.getSid(), formattedPhone);
            return true;

        } catch (Exception e) {
            String err = e.getMessage() != null ? e.getMessage() : e.toString();
            if (err.contains("not a valid message-capable Twilio phone number") || err.contains("is not a valid message-capable Twilio phone number")) {
                log.error("Failed to send SMS to {}: {}. Suggestion: ensure 'twilio.phone-number' is a Twilio phone number capable of sending SMS to the destination country (check Twilio Console -> Phone Numbers -> Manage).", to, err, e);
            } else {
                log.error("Failed to send SMS to {}: {}", to, err, e);
            }
            return false;
        }
    }

    /**
     * Sends a WhatsApp message.
     *
     * @param to Phone number (will be prefixed with whatsapp:)
     * @param message The message content
     * @return true if sent successfully, false otherwise
     */
    public boolean sendWhatsApp(String to, String message) {
        if (!isConfigured()) {
            log.debug("Twilio not configured - WhatsApp not sent to: {}", to);
            return false;
        }

        if (whatsappNumber == null || whatsappNumber.isEmpty()) {
            log.error("Twilio WhatsApp 'from' number is not configured. Set 'twilio.whatsapp-number' in config.");
            return false;
        }

        try {
            // Ensure phone number is in E.164 format
            String formattedPhone = formatPhoneNumber(to);

            // WhatsApp requires 'whatsapp:' prefix for both from and to
            PhoneNumber toWhatsApp = new PhoneNumber("whatsapp:" + formattedPhone);
            PhoneNumber fromWhatsApp = new PhoneNumber("whatsapp:" + whatsappNumber);

            Message twilioMessage = Message.creator(
                    toWhatsApp,
                    fromWhatsApp,
                    message
            ).create();

            log.info("WhatsApp sent successfully. SID: {}, To: {}",
                    twilioMessage.getSid(), formattedPhone);
            return true;

        } catch (Exception e) {
            // Provide more actionable logs for common Twilio errors
            String err = e.getMessage() != null ? e.getMessage() : e.toString();
            if (err.contains("could not find a Channel") || err.contains("Channel with the specified From address")) {
                log.error("Failed to send WhatsApp to {}: {}. Suggestion: ensure your Twilio WhatsApp sender (twilio.whatsapp-number) is correctly provisioned in Twilio Console and matches the configured value. If using Sandbox, follow Twilio WhatsApp Sandbox setup steps.", to, err, e);
            } else {
                log.error("Failed to send WhatsApp to {}: {}", to, err, e);
            }
            return false;
        }
    }

    /**
     * Sends an OTP via SMS.
     *
     * @param to Phone number
     * @param otp The OTP code
     * @param purpose Purpose of the OTP
     * @return true if sent successfully
     */
    public boolean sendOTPSMS(String to, String otp, String purpose) {
        String message = String.format(
                "Your OTP code for %s is: %s\n\nThis code will expire in 10 minutes.\n\nBidzaro Catering Platform",
                purpose, otp
        );
        return sendSMS(to, message);
    }

    /**
     * Sends an OTP via WhatsApp.
     *
     * @param to Phone number
     * @param otp The OTP code
     * @param purpose Purpose of the OTP
     * @return true if sent successfully
     */
    public boolean sendOTPWhatsApp(String to, String otp, String purpose) {
        String message = String.format(
                "Your OTP code for %s is: *%s*\n\nThis code will expire in 10 minutes.\n\n_Bidzaro Catering Platform_",
                purpose, otp
        );
        return sendWhatsApp(to, message);
    }

    /**
     * Sends order confirmation via WhatsApp.
     *
     * @param to Phone number
     * @param orderId Order ID
     * @param amount Order amount
     * @return true if sent successfully
     */
    public boolean sendOrderConfirmationWhatsApp(String to, String orderId, String amount) {
        String message = String.format(
                "✅ *Order Confirmed!*\n\n" +
                "Order ID: %s\n" +
                "Amount: %s\n\n" +
                "Thank you for your order!\n\n" +
                "_Bidzaro Catering Platform_",
                orderId, amount
        );
        return sendWhatsApp(to, message);
    }

    /**
     * Sends order status update via SMS.
     *
     * @param to Phone number
     * @param orderId Order ID
     * @param status New status
     * @return true if sent successfully
     */
    public boolean sendOrderStatusSMS(String to, String orderId, String status) {
        String message = String.format(
                "Order %s is now %s.\n\nBidzaro Catering Platform",
                orderId, status
        );
        return sendSMS(to, message);
    }

    /**
     * Formats phone number to E.164 format if not already.
     * E.164 format: +[country code][number] (e.g., +919640206605)
     *
     * @param phone The phone number
     * @return Formatted phone number
     */
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Remove all spaces, dashes, parentheses
        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        // If doesn't start with +, assume it needs country code
        if (!cleaned.startsWith("+")) {
            // If starts with 91 (India), add +
            if (cleaned.startsWith("91") && cleaned.length() == 12) {
                cleaned = "+" + cleaned;
            }
            // If starts with 1 (USA), add +
            else if (cleaned.startsWith("1") && cleaned.length() == 11) {
                cleaned = "+" + cleaned;
            }
            // If 10 digits, assume USA
            else if (cleaned.length() == 10) {
                cleaned = "+1" + cleaned;
            }
            // If 10 digits starting with 9, assume India
            else if (cleaned.startsWith("9") && cleaned.length() == 10) {
                cleaned = "+91" + cleaned;
            }
        }

        return cleaned;
    }

    /**
     * Checks if Twilio is configured and ready.
     *
     * @return true if configured
     */
    public boolean isConfigured() {
        return twilioReady;
    }
}

