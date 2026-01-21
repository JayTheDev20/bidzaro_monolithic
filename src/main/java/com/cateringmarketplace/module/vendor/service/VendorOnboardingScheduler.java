package com.cateringmarketplace.module.vendor.service;

import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    // Use the specific vendor setup URL provided
    private static final String VENDOR_SETUP_URL = "http://localhost:5173/vendor-setup";

    /**
     * Runs every minute to check for vendors who registered > 5 mins ago
     * but haven't created a profile.
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkPendingVendorProfiles() {
        log.debug("Checking for incomplete vendor profiles...");

        // Cutoff time: 5 minutes ago
        Instant cutoffTime = Instant.now().minus(5, ChronoUnit.MINUTES);

        // Find vendors who registered before cutoff and haven't been reminded
        List<User> pendingUsers = userRepository.findPendingVendorProfiles(cutoffTime);

        for (User user : pendingUsers) {
            try {
                // Check if they have already created a vendor profile
                boolean hasProfile = vendorRepository.existsByUserId(user.getUserId());

                if (!hasProfile) {
                    log.info("Sending profile completion reminder to user: {}", user.getUserId());
                    
                    // Send reminder email with the specific vendor setup link
                    emailService.sendProfileCompletionReminder(user.getEmail(), user.getFirstName(), VENDOR_SETUP_URL);

                    // Mark as reminded so we don't send again
                    user.setProfileReminderSent(true);
                    userRepository.save(user);
                } else {
                    // They have a profile, but flag was false. Update flag to true to skip next time.
                    user.setProfileReminderSent(true);
                    userRepository.save(user);
                }
            } catch (Exception e) {
                log.error("Failed to process pending vendor reminder for user {}: {}", user.getUserId(), e.getMessage());
            }
        }
    }
}
