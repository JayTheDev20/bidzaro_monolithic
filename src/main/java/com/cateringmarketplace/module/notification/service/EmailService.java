package com.cateringmarketplace.module.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${smtp.logo-url:https://cdn-icons-png.flaticon.com/512/9370/9370077.png}")
    private String logoUrl;

    /**
     * Sends a simple text email (wrapped in HTML template).
     */
    @Async("emailExecutor")
    public void sendSimpleEmail(String to, String subject, String body, String role) {
        try {
            log.info("Sending simple email (as HTML) to: {}", to);

            // Check if body seems to contain HTML tags, if so, don't replace newlines
            boolean isHtml = body.trim().startsWith("<") && body.trim().endsWith(">");
            String content = isHtml ? body : body.replace("\n", "<br>");

            String htmlBody = getHtmlTemplate(subject, content, role);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Generates a beautiful HTML template with a logo.
     */
    private String getHtmlTemplate(String title, String content, String role) {
        // Uses configured logoUrl (smtp.logo-url) or default if not set
        // Brand color: Orange (#F97316)

        String roleBadge = "";
        if (role != null && !role.isEmpty()) {
            String badgeColor = "#64748b"; // Default gray
            if (role.equalsIgnoreCase("VENDOR")) badgeColor = "#7c3aed"; // Purple
            else if (role.equalsIgnoreCase("ADMIN")) badgeColor = "#dc2626"; // Red
            else if (role.equalsIgnoreCase("USER")) badgeColor = "#2563eb"; // Blue

            roleBadge = String.format("""
                <div style="text-align: center; margin-bottom: 20px;">
                    <span style="background-color: %s; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: bold; text-transform: uppercase;">%s</span>
                </div>
            """, badgeColor, role);
        }

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333333;
                        margin: 0;
                        padding: 0;
                        background-color: #f7f9fa;
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #ffffff;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
                        border: 1px solid #e1e4e8;
                    }
                    .email-header {
                        background-color: #ffffff;
                        padding: 30px;
                        text-align: center;
                        border-bottom: 4px solid #F97316; /* Orange brand color */
                    }
                    .email-header img {
                        max-height: 60px;
                        object-fit: contain;
                    }
                    .email-body {
                        padding: 40px 30px;
                        background-color: #ffffff;
                    }
                    .email-title {
                        color: #1a1a1a;
                        font-size: 24px;
                        font-weight: 700;
                        margin-bottom: 10px;
                        text-align: center;
                    }
                    .email-content {
                        font-size: 16px;
                        color: #555555;
                        line-height: 1.8;
                    }
                    .email-footer {
                        background-color: #f8fafc;
                        padding: 25px;
                        text-align: center;
                        font-size: 13px;
                        color: #94a3b8;
                        border-top: 1px solid #edf2f7;
                    }
                    .email-footer p {
                        margin: 5px 0;
                    }
                    .highlight-box {
                        background-color: #FFF7ED; /* Light orange bg */
                        border: 1px solid #FDBA74; /* Orange border */
                        border-radius: 8px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .cta-button {
                        display: inline-block;
                        background-color: #F97316;
                        color: white;
                        padding: 12px 24px;
                        border-radius: 6px;
                        text-decoration: none;
                        font-weight: bold;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="email-header">
                        <img src="%s" alt="Business Logo">
                    </div>
                    <div class="email-body">
                        <h1 class="email-title">%s</h1>
                        %s
                        <div class="email-content">
                            %s
                        </div>
                    </div>
                    <div class="email-footer">
                        <p>&copy; 2026 Catering Platform. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, logoUrl, title, roleBadge, content);
    }

    /**
     * Sends an HTML email.
     */
    @Async("emailExecutor")
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
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
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Sends an email with CC and BCC.
     */
    @Async("emailExecutor")
    public void sendEmailWithCopies(String to, String[] cc, String[] bcc, String subject, String body, boolean isHtml, String role) {
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

            if (!isHtml) {
                String htmlBody = getHtmlTemplate(subject, body.replace("\n", "<br>"), role);
                helper.setText(htmlBody, true);
            } else {
                helper.setText(body, true);
            }

            mailSender.send(message);

            log.info("Email with copies sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with copies to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Sends a welcome email to new users.
     */
    public void sendWelcomeEmail(String to, String userName, String role) {
        String subject = "Welcome to Catering Platform!";
        String body = String.format("""
                <p>Dear %s,</p>
                
                <p>Welcome to <strong>Catering Platform</strong>! We're excited to have you on board.</p>
                
                <p>Your account has been successfully created. You can now:</p>
                <ul style="color: #475569;">
                    <li>Browse vendors and their menus</li>
                    <li>Create bid requests for your events</li>
                    <li>Manage your orders and payments</li>
                    <li>Track your loyalty points</li>
                </ul>
                
                <p>If you have any questions, feel free to contact our support team.</p>
                
                <p style="margin-top: 20px;">Best regards,<br>The Catering Platform Team</p>
                """, userName);

        sendSimpleEmail(to, subject, body, role);
    }

    /**
     * Sends an OTP email for verification.
     */
    public void sendOTPEmail(String to, String otp, String purpose) {
        String subject = "Your OTP Code - Catering Platform";

        // Create an HTML content block where the OTP is visually highlighted and centered
        // Using Orange theme for the OTP box
        String content = String.format("""
                <p style="margin:0 0 20px 0; font-size:16px;">Hello,</p>
                <p style="margin:0 0 20px 0; font-size:16px;">We received a request for <strong>%s</strong>. Please use the following One-Time Password (OTP) to proceed:</p>
                
                <div style="text-align:center; margin:30px 0;">
                    <div style="display:inline-block; padding:20px 40px; background-color:#FFF7ED; border: 2px dashed #F97316; border-radius:12px;">
                        <span style="font-size:32px; font-weight:800; color:#F97316; letter-spacing:8px; font-family: 'Courier New', monospace;">%s</span>
                    </div>
                </div>
                
                <p style="margin:20px 0 0 0; font-size:14px; color:#666;">This code is valid for <strong>10 minutes</strong>. Do not share this code with anyone.</p>
                <p style="margin:10px 0 0 0; font-size:14px; color:#999;">If you didn't request this code, please ignore this email.</p>
                """, purpose, otp);

        sendSimpleEmail(to, subject, content, null);
    }

    /**
     * Sends a password reset email.
     */
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request - Catering Platform";
        String resetLink = "https://cateringplatform.com/reset-password?token=" + resetToken;

        String body = String.format("""
                We received a request to reset your password.
                
                Click the link below to reset your password:
                <br><br>
                <a href="%s" class="cta-button" style="color: white;">Reset Password</a>
                <br><br>
                This link will expire in 1 hour.
                
                If you didn't request this, please ignore this email.
                
                Best regards,
                The Catering Platform Team
                """, resetLink);

        sendSimpleEmail(to, subject, body, null);
    }

    /**
     * Sends an order confirmation email.
     */
    public void sendOrderConfirmationEmail(String to, String orderId, String orderDetails, String role) {
        String subject = "Order Confirmation - " + orderId;
        String body = String.format("""
                Your order has been confirmed!
                
                <div class="highlight-box">
                    <strong>Order ID: %s</strong>
                </div>
                
                %s
                
                You will receive updates on your order status via email and notifications.
                
                Thank you for choosing Catering Platform!
                
                Best regards,
                The Catering Platform Team
                """, orderId, orderDetails);

        sendSimpleEmail(to, subject, body, role);
    }

    /**
     * Sends a bid acceptance notification email.
     */
    public void sendBidAcceptanceEmail(String to, String bidId, String vendorName, String role) {
        String subject = "Your Bid Has Been Accepted!";
        String body = String.format("""
                Congratulations! Your bid has been accepted.
                
                <div class="highlight-box">
                    <strong>Bid ID: %s</strong><br>
                    Vendor: %s
                </div>
                
                Please proceed to make the token payment to confirm your order.
                
                Best regards,
                The Catering Platform Team
                """, bidId, vendorName);

        sendSimpleEmail(to, subject, body, role);
    }

    /**
     * Sends a payment confirmation email.
     */
    public void sendPaymentConfirmationEmail(String to, String transactionId, double amount, String paymentType, String role) {
        String subject = "Payment Confirmation - " + transactionId;
        String body = String.format("""
                Your payment has been successfully processed.
                
                <div class="highlight-box">
                    Transaction ID: %s<br>
                    Amount: $%.2f USD<br>
                    Payment Type: %s
                </div>
                
                Thank you for your payment!
                
                Best regards,
                The Catering Platform Team
                """, transactionId, amount, paymentType);

        sendSimpleEmail(to, subject, body, role);
    }

    /**
     * Sends a vendor approval email.
     */
    public void sendVendorApprovalEmail(String to, String vendorName) {
        String subject = "Vendor Account Approved!";
        String body = String.format("""
                Congratulations %s!
                
                Your vendor account has been approved. You can now:
                <ul>
                    <li>Add your menu items</li>
                    <li>Receive and respond to bid requests</li>
                    <li>Manage your orders</li>
                    <li>Track your earnings</li>
                </ul>
                
                <a href="https://vendor.cateringplatform.com/login" class="cta-button" style="color: white;">Go to Vendor Dashboard</a>
                
                Best regards,
                The Catering Platform Team
                """, vendorName);

        sendSimpleEmail(to, subject, body, "VENDOR");
    }

    /**
     * Sends a vendor rejection email.
     */
    public void sendVendorRejectionEmail(String to, String vendorName, String reason) {
        String subject = "Vendor Account Application Status";
        String body = String.format("""
                Dear %s,
                
                Thank you for your interest in joining Catering Platform.
                
                Unfortunately, we are unable to approve your vendor account at this time.
                
                <div class="highlight-box" style="background-color: #FEF2F2; border-color: #F87171;">
                    <strong>Reason:</strong> %s
                </div>
                
                If you have any questions or would like to reapply, please contact our support team.
                
                Best regards,
                The Catering Platform Team
                """, vendorName, reason);

        sendSimpleEmail(to, subject, body, "VENDOR");
    }

    /**
     * Sends a notification email (generic).
     */
    public void sendNotificationEmail(String to, String title, String message, String role) {
        sendSimpleEmail(to, title, message, role);
    }

    /**
     * Sends a profile completion reminder email.
     */
    public void sendProfileCompletionReminder(String to, String userName, String loginLink) {
        String subject = "Complete Your Vendor Profile - Action Required";
        String body = String.format("""
                <p>Dear %s,</p>
                
                <p>We noticed you registered as a vendor but haven't completed your profile yet.</p>
                
                <p>To start receiving bid requests and growing your business, you need to complete your vendor profile setup.</p>
                
                <div class="highlight-box">
                    <strong>Don't miss out on potential customers!</strong>
                </div>
                
                <p>Click the button below to log in and complete your profile:</p>
                
                <a href="%s" class="cta-button" style="color: white;">Complete Profile Now</a>
                
                <p style="margin-top: 20px;">If you need any assistance, please contact our support team.</p>
                
                <p>Best regards,<br>The Catering Platform Team</p>
                """, userName, loginLink);

        sendSimpleEmail(to, subject, body, "VENDOR");
    }

    /**
     * Sends an account locked email.
     */
    public void sendAccountLockedEmail(String to, String userName, String resetLink) {
        String subject = "Account Locked - Action Required";
        String body = String.format("""
                <p>Dear %s,</p>
                
                <p>Your account has been temporarily locked due to multiple failed login attempts.</p>
                
                <p>To unlock your account, please reset your password using the link below:</p>
                
                <a href="%s" class="cta-button" style="color: white;">Reset Password</a>
                
                <p>If you did not attempt to log in, please contact our support team immediately.</p>
                
                <p>Best regards,<br>The Catering Platform Team</p>
                """, userName, resetLink);

        sendSimpleEmail(to, subject, body, null);
    }
}
