package com.cateringmarketplace.module.vendor.service;

import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.notification.service.TwilioService;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduler to check for incomplete vendor profiles and send reminders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorOnboardingScheduler {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final TwilioService twilioService;

    // Use the specific vendor setup URL provided
    private static final String VENDOR_SETUP_URL = "http://localhost:5173/vendor-setup";

    /**
     * Runs every minute to check for vendors who haven't created a profile.
     * Implements multi-stage reminder logic:
     * 1. Initial: 5 mins after registration
     * 2. Hourly: 3 times (1 hour interval)
     * 3. Daily: 3 times (24 hour interval)
     * 4. Cleanup: Delete user if still incomplete after 24h from last reminder
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkPendingVendorProfiles() {
        log.debug("Checking for incomplete vendor profiles...");

        // Fetch all vendors who haven't completed the full reminder cycle (count <= 7)
        List<User> pendingUsers = userRepository.findVendorsEligibleForReminderCheck();

        for (User user : pendingUsers) {
            try {
                // Check if they have already created a vendor profile
                boolean hasProfile = vendorRepository.existsByUserId(user.getUserId());

                if (hasProfile) {
                    // Profile created, stop reminding
                    user.setProfileReminderCount(99);
                    userRepository.save(user);
                    continue;
                }

                processReminderLogic(user);

            } catch (Exception e) {
                log.error("Failed to process pending vendor reminder for user {}: {}", user.getUserId(), e.getMessage());
            }
        }
    }

    private void processReminderLogic(User user) {
        Instant now = Instant.now();
        Instant registeredAt = user.getCreatedAt();
        Instant lastSent = user.getLastReminderSentAt();
        int count = user.getProfileReminderCount() != null ? user.getProfileReminderCount() : 0;

        boolean shouldSend = false;
        boolean shouldDelete = false;

        // Stage 1: Initial Reminder (5 mins after registration)
        if (count == 0) {
            if (registeredAt.isBefore(now.minus(5, ChronoUnit.MINUTES))) {
                shouldSend = true;
            }
        }
        // Stage 2: Hourly Reminders (Count 1, 2, 3) -> 1 hour after last reminder
        else if (count >= 1 && count < 4) {
            if (lastSent != null && lastSent.isBefore(now.minus(1, ChronoUnit.HOURS))) {
                shouldSend = true;
            }
        }
        // Stage 3: Daily Reminders (Count 4, 5, 6) -> 24 hours after last reminder
        else if (count >= 4 && count < 7) {
            if (lastSent != null && lastSent.isBefore(now.minus(24, ChronoUnit.HOURS))) {
                shouldSend = true;
            }
        }
        // Stage 4: Cleanup (Count 7) -> 24 hours after last reminder
        else if (count >= 7) {
            if (lastSent != null && lastSent.isBefore(now.minus(24, ChronoUnit.HOURS))) {
                shouldDelete = true;
            }
        }

        if (shouldDelete) {
            log.warn("Deleting abandoned vendor registration: {}", user.getUserId());
            userRepository.delete(user);
            // Also delete OTPs if any
            // otpVerificationRepository.deleteByIdentifier(user.getEmail()); // Optional cleanup
        } else if (shouldSend) {
            sendReminder(user);
            user.setProfileReminderCount(count + 1);
            user.setLastReminderSentAt(now);
            userRepository.save(user);
            log.info("Sent reminder #{} to user {}", count + 1, user.getUserId());
        }
    }

    private void sendReminder(User user) {
        // 1. Send Email
        emailService.sendProfileCompletionReminder(user.getEmail(), user.getFirstName(), VENDOR_SETUP_URL);

        // 2. Send WhatsApp/SMS
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            twilioService.sendProfileReminder(user.getPhone(), user.getFirstName(), VENDOR_SETUP_URL);
        }
    }
}
