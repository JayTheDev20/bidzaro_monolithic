package com.cateringmarketplace.module.referral.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.loyalty.service.LoyaltyService;
import com.cateringmarketplace.module.referral.dto.response.ReferralCodeResponse;
import com.cateringmarketplace.module.referral.dto.response.ReferralStatsResponse;
import com.cateringmarketplace.module.referral.dto.response.ReferralStatsResponse.ReferralDetail;
import com.cateringmarketplace.module.referral.model.ReferralCode;
import com.cateringmarketplace.module.referral.model.ReferralCode.ReferralStatus;
import com.cateringmarketplace.module.referral.model.ReferralEvent;
import com.cateringmarketplace.module.referral.model.ReferralEvent.EventType;
import com.cateringmarketplace.module.referral.model.ReferralEvent.RewardStatus;
import com.cateringmarketplace.module.referral.repository.ReferralCodeRepository;
import com.cateringmarketplace.module.referral.repository.ReferralEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for referral operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralService {

    private final ReferralCodeRepository referralCodeRepository;
    private final ReferralEventRepository referralEventRepository;
    private final UserRepository userRepository;
    private final LoyaltyService loyaltyService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${referral.referrer-reward-points:500}")
    private int referrerRewardPoints;

    @Value("${referral.referred-reward-points:200}")
    private int referredRewardPoints;

    /**
     * Gets or creates referral code for a user.
     */
    @Transactional
    public ReferralCodeResponse getOrCreateReferralCode(String userId) {
        log.info("Getting referral code for user: {}", userId);

        ReferralCode code = referralCodeRepository.findByUserId(userId)
                .orElseGet(() -> createReferralCode(userId));

        return ReferralCodeResponse.fromEntity(code, frontendUrl);
    }

    /**
     * Creates a new referral code for a user.
     */
    private ReferralCode createReferralCode(String userId) {
        String generatedCode = ReferralCode.generateCode(userId);

        // Ensure unique code
        int attempts = 0;
        while (referralCodeRepository.existsByCodeIgnoreCase(generatedCode) && attempts < 5) {
            generatedCode = "REF" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            attempts++;
        }

        ReferralCode code = ReferralCode.builder()
                .codeId(UUID.randomUUID().toString())
                .userId(userId)
                .code(generatedCode)
                .status(ReferralStatus.ACTIVE)
                .build();

        return referralCodeRepository.save(code);
    }

    /**
     * Processes a referral when a new user signs up with a referral code.
     */
    @Transactional
    public void processReferralSignup(String referredUserId, String referralCode) {
        log.info("Processing referral signup for user: {} with code: {}", referredUserId, referralCode);

        if (referralCode == null || referralCode.isEmpty()) {
            return;
        }

        // Check if user was already referred
        if (referralEventRepository.existsByReferredUserId(referredUserId)) {
            log.warn("User {} was already referred", referredUserId);
            return;
        }

        // Find referral code
        ReferralCode code = referralCodeRepository.findByCodeIgnoreCaseAndStatus(referralCode, ReferralStatus.ACTIVE)
                .orElse(null);

        if (code == null) {
            log.warn("Invalid referral code: {}", referralCode);
            return;
        }

        // Cannot refer yourself
        if (code.getUserId().equals(referredUserId)) {
            log.warn("User tried to refer themselves");
            return;
        }

        // Create referral event
        ReferralEvent event = ReferralEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .referrerUserId(code.getUserId())
                .referredUserId(referredUserId)
                .referralCode(referralCode)
                .eventType(EventType.SIGNUP)
                .rewardStatus(RewardStatus.PENDING)
                .referrerRewardPoints(referrerRewardPoints)
                .referredRewardPoints(referredRewardPoints)
                .build();

        referralEventRepository.save(event);

        // Update referral code stats
        code.setTotalReferrals(code.getTotalReferrals() + 1);
        referralCodeRepository.save(code);

        // Give welcome bonus to referred user
        loyaltyService.addBonusPoints(referredUserId, referredRewardPoints,
                "Welcome bonus for joining via referral", event.getEventId());

        log.info("Referral signup processed: referrer={}, referred={}", code.getUserId(), referredUserId);
    }

    /**
     * Completes a referral when referred user places first order.
     */
    @Transactional
    public void completeReferral(String referredUserId, String orderId) {
        log.info("Completing referral for user: {} on order: {}", referredUserId, orderId);

        // Find referral event
        ReferralEvent event = referralEventRepository.findByReferredUserId(referredUserId)
                .orElse(null);

        if (event == null) {
            log.info("No referral found for user: {}", referredUserId);
            return;
        }

        if (event.getRewardStatus() == RewardStatus.GRANTED) {
            log.info("Referral already completed for user: {}", referredUserId);
            return;
        }

        // Update event
        event.setEventType(EventType.FIRST_ORDER_COMPLETED);
        event.setRewardStatus(RewardStatus.GRANTED);
        event.setOrderId(orderId);
        event.setRewardedAt(Instant.now());
        referralEventRepository.save(event);

        // Grant reward to referrer
        loyaltyService.addBonusPoints(event.getReferrerUserId(), event.getReferrerRewardPoints(),
                "Referral bonus - friend completed first order", event.getEventId());

        // Update referral code stats
        ReferralCode code = referralCodeRepository.findByUserId(event.getReferrerUserId())
                .orElse(null);
        if (code != null) {
            code.setSuccessfulReferrals(code.getSuccessfulReferrals() + 1);
            code.setTotalRewardsEarned(code.getTotalRewardsEarned() + event.getReferrerRewardPoints());
            referralCodeRepository.save(code);
        }

        log.info("Referral completed: {} points awarded to referrer {}",
                event.getReferrerRewardPoints(), event.getReferrerUserId());
    }

    /**
     * Gets referral statistics for a user.
     */
    public ReferralStatsResponse getReferralStats(String userId) {
        ReferralCode code = referralCodeRepository.findByUserId(userId)
                .orElse(null);

        if (code == null) {
            return ReferralStatsResponse.builder()
                    .totalInvites(0)
                    .signups(0)
                    .completedOrders(0)
                    .pendingRewards(0)
                    .grantedRewards(0)
                    .totalPointsEarned(0)
                    .recentReferrals(List.of())
                    .build();
        }

        long pendingCount = referralEventRepository.countByReferrerUserIdAndRewardStatus(userId, RewardStatus.PENDING);
        long grantedCount = referralEventRepository.countByReferrerUserIdAndRewardStatus(userId, RewardStatus.GRANTED);
        long completedOrderCount = referralEventRepository.countByReferrerUserIdAndEventType(userId, EventType.FIRST_ORDER_COMPLETED);

        // Get recent referrals
        List<ReferralEvent> recentEvents = referralEventRepository
                .findByReferrerUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 5))
                .getContent();

        List<ReferralDetail> recentReferrals = recentEvents.stream()
                .map(e -> {
                    User referred = userRepository.findByUserId(e.getReferredUserId()).orElse(null);
                    String name = referred != null ?
                            referred.getFirstName() + " " + (referred.getLastName() != null ? referred.getLastName().charAt(0) + "." : "")
                            : "User";

                    return ReferralDetail.builder()
                            .referredUserName(name)
                            .eventType(e.getEventType().name())
                            .rewardStatus(e.getRewardStatus().name())
                            .rewardPoints(e.getReferrerRewardPoints())
                            .createdAt(e.getCreatedAt().toString())
                            .build();
                })
                .collect(Collectors.toList());

        return ReferralStatsResponse.builder()
                .referralCode(code.getCode())
                .totalInvites(code.getTotalReferrals())
                .signups(code.getTotalReferrals())
                .completedOrders((int) completedOrderCount)
                .pendingRewards((int) pendingCount)
                .grantedRewards((int) grantedCount)
                .totalPointsEarned(code.getTotalRewardsEarned())
                .recentReferrals(recentReferrals)
                .build();
    }

    /**
     * Validates a referral code.
     */
    public boolean isValidReferralCode(String code) {
        return referralCodeRepository.findByCodeIgnoreCaseAndStatus(code, ReferralStatus.ACTIVE).isPresent();
    }
}

