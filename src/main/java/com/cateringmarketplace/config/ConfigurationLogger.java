package com.cateringmarketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Logs all configuration details on application startup.
 */
@Component
@Slf4j
public class ConfigurationLogger {

    // Server
    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    // MongoDB
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    // Redis
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // SMTP
    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${smtp.from-email}")
    private String smtpFromEmail;

    // JWT
    @Value("${jwt.access-token-expiration}")
    private long jwtAccessExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long jwtRefreshExpiration;

    // Razorpay
    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    // Stripe
    @Value("${stripe.publishable-key}")
    private String stripePublishableKey;

    // Twilio
    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${twilio.whatsapp-number}")
    private String twilioWhatsappNumber;

    // Firebase
    @Value("${firebase.credentials-path}")
    private String firebaseCredentialsPath;

    @Value("${firebase.project-id}")
    private String firebaseProjectId;

    // GCP
    @Value("${gcp.project-id}")
    private String gcpProjectId;

    @Value("${gcp.storage-bucket}")
    private String gcpStorageBucket;

    // App Settings
    @Value("${app.default-country}")
    private String defaultCountry;

    @Value("${app.default-currency}")
    private String defaultCurrency;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // File Upload
    @Value("${app-upload.path}")
    private String uploadPath;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @EventListener(ApplicationReadyEvent.class)
    public void logConfiguration() {
        log.info("════════════════════════════════════════════════════════════════");
        log.info("🚀 BIDZARO CATERING PLATFORM - CONFIGURATION LOADED");
        log.info("════════════════════════════════════════════════════════════════");

        // Server
        log.info("📡 SERVER CONFIGURATION:");
        log.info("   └─ Port: {}", serverPort);
        log.info("   └─ Context Path: {}", contextPath);
        log.info("   └─ Base URL: http://localhost:{}{}", serverPort, contextPath);
        log.info("   └─ Swagger UI: http://localhost:{}{}/swagger-ui.html", serverPort, contextPath);
        log.info("");

        // Database
        log.info("💾 DATABASE CONFIGURATION:");
        log.info("   └─ MongoDB URI: {}", maskSensitiveUrl(mongoUri));
        log.info("   └─ MongoDB Database: {}", mongoDatabase);
        log.info("   └─ MongoDB Status: {}", isConfigured(mongoUri) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // Cache
        log.info("⚡ CACHE CONFIGURATION:");
        log.info("   └─ Redis Host: {}", redisHost);
        log.info("   └─ Redis Port: {}", redisPort);
        log.info("   └─ Redis Status: {}", isConfigured(redisHost) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // Email
        log.info("📧 EMAIL CONFIGURATION (SMTP):");
        log.info("   └─ Host: {}", smtpHost);
        log.info("   └─ Port: {}", smtpPort);
        log.info("   └─ Username: {}", maskEmail(smtpUsername));
        log.info("   └─ From Email: {}", smtpFromEmail);
        log.info("   └─ Status: {}", isConfigured(smtpUsername) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // Payment Gateways
        log.info("💳 PAYMENT GATEWAYS:");
        log.info("   ├─ Razorpay (India - INR):");
        log.info("   │  └─ Key ID: {}", maskKey(razorpayKeyId));
        log.info("   │  └─ Status: {}", isConfigured(razorpayKeyId) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("   └─ Stripe (USA - USD):");
        log.info("      └─ Publishable Key: {}", maskKey(stripePublishableKey));
        log.info("      └─ Status: {}", isConfigured(stripePublishableKey) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // SMS & WhatsApp
        log.info("📱 SMS & WHATSAPP (Twilio):");
        log.info("   └─ Account SID: {}", maskKey(twilioAccountSid));
        log.info("   └─ Phone Number: {}", twilioPhoneNumber);
        log.info("   └─ WhatsApp Number: {}", twilioWhatsappNumber);
        log.info("   └─ Status: {}", isConfigured(twilioAccountSid) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // Push Notifications
        log.info("🔔 PUSH NOTIFICATIONS (Firebase):");
        log.info("   └─ Credentials Path: {}", firebaseCredentialsPath);
        log.info("   └─ Project ID: {}", firebaseProjectId);
        log.info("   └─ Status: {}", isConfigured(firebaseProjectId) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // Cloud Storage
        log.info("☁️ CLOUD STORAGE (Google Cloud Platform):");
        log.info("   └─ Project ID: {}", gcpProjectId);
        log.info("   └─ Storage Bucket: {}", gcpStorageBucket);
        log.info("   └─ Status: {}", isConfigured(gcpProjectId) ? "✅ Configured" : "⚠️ Not Configured");
        log.info("");

        // JWT
        log.info("🔐 SECURITY (JWT):");
        log.info("   └─ Access Token Expiration: {} ms ({} minutes)", jwtAccessExpiration, jwtAccessExpiration / 60000);
        log.info("   └─ Refresh Token Expiration: {} ms ({} days)", jwtRefreshExpiration, jwtRefreshExpiration / 86400000);
        log.info("   └─ Status: ✅ Configured");
        log.info("");

        // Platform Settings
        log.info("🌍 PLATFORM SETTINGS:");
        log.info("   └─ Default Country: {} 🇺🇸", defaultCountry);
        log.info("   └─ Default Currency: {} 💵", defaultCurrency);
        log.info("   └─ Frontend URL: {}", frontendUrl);
        log.info("");

        // File Upload
        log.info("📤 FILE UPLOAD:");
        log.info("   └─ Upload Path: {}", uploadPath);
        log.info("   └─ Max File Size: {}", maxFileSize);
        log.info("   └─ Status: ✅ Configured");
        log.info("");

        log.info("════════════════════════════════════════════════════════════════");
        log.info("✅ ALL CONFIGURATIONS LOADED SUCCESSFULLY!");
        log.info("🚀 Application is ready to accept requests");
        log.info("════════════════════════════════════════════════════════════════");
    }

    private boolean isConfigured(String value) {
        return value != null && !value.isEmpty() &&
               !value.startsWith("your_") &&
               !value.equals("localhost");
    }

    private String maskSensitiveUrl(String url) {
        if (url == null || url.isEmpty()) return "Not set";
        if (url.contains("@")) {
            return url.replaceAll("://([^:]+):([^@]+)@", "://***:***@");
        }
        return url;
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "Not set";
        if (email.contains("@")) {
            String[] parts = email.split("@");
            if (parts[0].length() > 2) {
                return parts[0].substring(0, 2) + "***@" + parts[1];
            }
        }
        return email;
    }

    private String maskKey(String key) {
        if (key == null || key.isEmpty()) return "Not set";
        if (key.startsWith("your_")) return "⚠️ Not configured (placeholder)";
        if (key.length() > 8) {
            return key.substring(0, 8) + "..." + key.substring(key.length() - 4);
        }
        return key;
    }
}

