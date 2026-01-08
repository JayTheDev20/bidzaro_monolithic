package com.cateringmarketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduling configuration for background jobs.
 * Enables scheduled tasks for bid expiry, order status updates, etc.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Scheduling is enabled via @EnableScheduling
    // Individual scheduled tasks are defined in the scheduler package
}

