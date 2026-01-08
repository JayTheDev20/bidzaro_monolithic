package com.cateringmarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for the Catering Marketplace Platform.
 *
 * <p>This is a production-ready Spring Boot REST API application for a
 * Catering Marketplace with bidding functionality, similar to Swiggy/Zomato
 * but for catering services with a reverse auction system.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>JWT-based authentication with refresh tokens</li>
 *   <li>WebSocket for real-time chat functionality</li>
 *   <li>MongoDB for data persistence</li>
 *   <li>Redis for caching and rate limiting</li>
 *   <li>External integrations: Razorpay, SendGrid, Twilio, Firebase, GCS</li>
 * </ul>
 *
 * @author Catering Platform Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class CateringPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CateringPlatformApplication.class, args);
    }
}

