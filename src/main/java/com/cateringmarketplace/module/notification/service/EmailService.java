package com.cateringmarketplace.module.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails via SMTP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${smtp.from-email:noreply@cateringplatform.com}")
    private String fromEmail;

    @Value("${smtp.from-name:Catering Platform}")
    private String fromName;

    /**
     * Sends a simple text email.
     */
    @Async("emailExecutor")
    public boolean sendSimpleEmail(String to, String subject, String body) {
        try {
            log.info("Sending simple email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(String.format("%s <%s>", fromName, fromEmail));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sends an HTML email.
     */
    @Async("emailExecutor")
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            log.info("Sending HTML email to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("HTML email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sends an email with CC and BCC.
     */
    @Async("emailExecutor")
    public boolean sendEmailWithCopies(String to, String[] cc, String[] bcc, String subject, String body, boolean isHtml) {
        try {
            log.info("Sending email with copies to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(to);
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            if (bcc != null && bcc.length > 0) {
                helper.setBcc(bcc);
            }
            helper.setSubject(subject);
            helper.setText(body, isHtml);

            mailSender.send(message);

            log.info("Email with copies sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send email with copies to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sends a welcome email to new users.
     */
    public boolean sendWelcomeEmail(String to, String userName) {
        String subject = "Welcome to Catering Platform!";
        String body = String.format("""
                Dear %s,
                
                Welcome to Catering Platform! We're excited to have you on board.
                
                Your account has been successfully created. You can now:
                - Browse vendors and their menus
                - Create bid requests for your events
                - Manage your orders and payments
                - Track your loyalty points
                
                If you have any questions, feel free to contact our support team.
                
                Best regards,
                The Catering Platform Team
                """, userName);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends an OTP email for verification.
     */
    public boolean sendOTPEmail(String to, String otp, String purpose) {
        String subject = "Your OTP Code - Catering Platform";
        String body = String.format("""
                Your OTP code for %s is: %s
                
                This code will expire in 10 minutes.
                
                If you didn't request this code, please ignore this email.
                
                Best regards,
                The Catering Platform Team
                """, purpose, otp);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a password reset email.
     */
    public boolean sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request - Catering Platform";
        String resetLink = "https://cateringplatform.com/reset-password?token=" + resetToken;

        String body = String.format("""
                We received a request to reset your password.
                
                Click the link below to reset your password:
                %s
                
                This link will expire in 1 hour.
                
                If you didn't request this, please ignore this email.
                
                Best regards,
                The Catering Platform Team
                """, resetLink);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends an order confirmation email.
     */
    public boolean sendOrderConfirmationEmail(String to, String orderId, String orderDetails) {
        String subject = "Order Confirmation - " + orderId;
        String body = String.format("""
                Your order has been confirmed!
                
                Order ID: %s
                
                %s
                
                You will receive updates on your order status via email and notifications.
                
                Thank you for choosing Catering Platform!
                
                Best regards,
                The Catering Platform Team
                """, orderId, orderDetails);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a bid acceptance notification email.
     */
    public boolean sendBidAcceptanceEmail(String to, String bidId, String vendorName) {
        String subject = "Your Bid Has Been Accepted!";
        String body = String.format("""
                Congratulations! Your bid has been accepted.
                
                Bid ID: %s
                Vendor: %s
                
                Please proceed to make the token payment to confirm your order.
                
                Best regards,
                The Catering Platform Team
                """, bidId, vendorName);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a payment confirmation email.
     */
    public boolean sendPaymentConfirmationEmail(String to, String transactionId, double amount, String paymentType) {
        String subject = "Payment Confirmation - " + transactionId;
        String body = String.format("""
                Your payment has been successfully processed.
                
                Transaction ID: %s
                Amount: ₹%.2f
                Payment Type: %s
                
                Thank you for your payment!
                
                Best regards,
                The Catering Platform Team
                """, transactionId, amount, paymentType);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a vendor approval email.
     */
    public boolean sendVendorApprovalEmail(String to, String vendorName) {
        String subject = "Vendor Account Approved!";
        String body = String.format("""
                Congratulations %s!
                
                Your vendor account has been approved. You can now:
                - Add your menu items
                - Receive and respond to bid requests
                - Manage your orders
                - Track your earnings
                
                Log in to your account to get started.
                
                Best regards,
                The Catering Platform Team
                """, vendorName);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a vendor rejection email.
     */
    public boolean sendVendorRejectionEmail(String to, String vendorName, String reason) {
        String subject = "Vendor Account Application Status";
        String body = String.format("""
                Dear %s,
                
                Thank you for your interest in joining Catering Platform.
                
                Unfortunately, we are unable to approve your vendor account at this time.
                
                Reason: %s
                
                If you have any questions or would like to reapply, please contact our support team.
                
                Best regards,
                The Catering Platform Team
                """, vendorName, reason);

        return sendSimpleEmail(to, subject, body);
    }

    /**
     * Sends a notification email (generic).
     */
    public boolean sendNotificationEmail(String to, String title, String message) {
        return sendSimpleEmail(to, title, message);
    }
}

