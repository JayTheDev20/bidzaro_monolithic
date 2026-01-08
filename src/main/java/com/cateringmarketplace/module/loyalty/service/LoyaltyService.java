package com.cateringmarketplace.module.loyalty.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.loyalty.dto.response.LoyaltyBalanceResponse;
import com.cateringmarketplace.module.loyalty.dto.response.LoyaltyTransactionResponse;
import com.cateringmarketplace.module.loyalty.model.LoyaltyInfo;
import com.cateringmarketplace.module.loyalty.model.LoyaltyInfo.LoyaltyTier;
import com.cateringmarketplace.module.loyalty.model.LoyaltyTransaction;
import com.cateringmarketplace.module.loyalty.model.LoyaltyTransaction.TransactionType;
import com.cateringmarketplace.module.loyalty.repository.LoyaltyTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service class for loyalty points operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final UserRepository userRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    @Value("${loyalty.points-per-rupee:1}")
    private int pointsPerRupee;

    @Value("${loyalty.rupees-per-point:0.25}")
    private BigDecimal rupeesPerPoint;

    @Value("${loyalty.max-redeem-percentage:50}")
    private int maxRedeemPercentage;

    @Value("${loyalty.points-expiry-days:365}")
    private int pointsExpiryDays;

    /**
     * Gets loyalty balance for a user.
     */
    public LoyaltyBalanceResponse getBalance(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoyaltyInfo loyalty = user.getLoyalty();
        if (loyalty == null) {
            loyalty = initializeLoyalty(user);
        }

        return LoyaltyBalanceResponse.fromLoyaltyInfo(loyalty, rupeesPerPoint);
    }

    /**
     * Gets loyalty transaction history.
     */
    public Page<LoyaltyTransactionResponse> getTransactions(String userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(LoyaltyTransactionResponse::fromEntity);
    }

    /**
     * Earns points for a completed order.
     */
    @Transactional
    public LoyaltyTransaction earnPoints(String userId, String orderId, BigDecimal orderAmount, String description) {
        log.info("Earning points for user {} on order {}", userId, orderId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoyaltyInfo loyalty = user.getLoyalty();
        if (loyalty == null) {
            loyalty = initializeLoyalty(user);
        }

        // Calculate points with tier multiplier
        int basePoints = orderAmount.intValue() * pointsPerRupee;
        int earnedPoints = (int) (basePoints * loyalty.getEarnMultiplier());

        // Update balance
        loyalty.setPointsBalance(loyalty.getPointsBalance() + earnedPoints);
        loyalty.setLifetimePoints(loyalty.getLifetimePoints() + earnedPoints);
        loyalty.updateTier();

        user.setLoyalty(loyalty);
        userRepository.save(user);

        // Create transaction record
        LoyaltyTransaction txn = LoyaltyTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .userId(userId)
                .type(TransactionType.EARN)
                .points(earnedPoints)
                .balanceAfter(loyalty.getPointsBalance())
                .description(description != null ? description : "Points earned from order")
                .orderId(orderId)
                .expiresAt(Instant.now().plus(pointsExpiryDays, ChronoUnit.DAYS))
                .build();

        transactionRepository.save(txn);
        log.info("Earned {} points for user {}", earnedPoints, userId);

        return txn;
    }

    /**
     * Redeems points for order discount.
     */
    @Transactional
    public RedemptionResult redeemPoints(String userId, String orderId, int pointsToRedeem, BigDecimal orderAmount) {
        log.info("Redeeming {} points for user {} on order {}", pointsToRedeem, userId, orderId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoyaltyInfo loyalty = user.getLoyalty();
        if (loyalty == null || loyalty.getPointsBalance() < pointsToRedeem) {
            throw new BadRequestException("INSUFFICIENT_POINTS", "Insufficient loyalty points");
        }

        // Calculate max redeemable based on order amount
        BigDecimal maxRedeemValue = orderAmount.multiply(BigDecimal.valueOf(maxRedeemPercentage))
                .divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
        int maxRedeemablePoints = maxRedeemValue.divide(rupeesPerPoint, RoundingMode.DOWN).intValue();

        if (pointsToRedeem > maxRedeemablePoints) {
            throw new BadRequestException("EXCEED_MAX_REDEEM",
                    "Maximum redeemable points for this order: " + maxRedeemablePoints);
        }

        if (pointsToRedeem > loyalty.getPointsBalance()) {
            throw new BadRequestException("INSUFFICIENT_POINTS", "Insufficient loyalty points");
        }

        // Calculate discount value
        BigDecimal discountValue = BigDecimal.valueOf(pointsToRedeem).multiply(rupeesPerPoint);

        // Update balance
        loyalty.setPointsBalance(loyalty.getPointsBalance() - pointsToRedeem);
        user.setLoyalty(loyalty);
        userRepository.save(user);

        // Create transaction record
        LoyaltyTransaction txn = LoyaltyTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .userId(userId)
                .type(TransactionType.REDEEM)
                .points(-pointsToRedeem)
                .balanceAfter(loyalty.getPointsBalance())
                .description("Points redeemed for order discount")
                .orderId(orderId)
                .build();

        transactionRepository.save(txn);
        log.info("Redeemed {} points (₹{}) for user {}", pointsToRedeem, discountValue, userId);

        return new RedemptionResult(pointsToRedeem, discountValue, loyalty.getPointsBalance());
    }

    /**
     * Adds bonus points (signup, referral, etc.).
     */
    @Transactional
    public LoyaltyTransaction addBonusPoints(String userId, int points, String description, String referenceId) {
        log.info("Adding {} bonus points to user {}", points, userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoyaltyInfo loyalty = user.getLoyalty();
        if (loyalty == null) {
            loyalty = initializeLoyalty(user);
        }

        loyalty.setPointsBalance(loyalty.getPointsBalance() + points);
        loyalty.setLifetimePoints(loyalty.getLifetimePoints() + points);
        loyalty.updateTier();

        user.setLoyalty(loyalty);
        userRepository.save(user);

        LoyaltyTransaction txn = LoyaltyTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .userId(userId)
                .type(TransactionType.BONUS)
                .points(points)
                .balanceAfter(loyalty.getPointsBalance())
                .description(description)
                .referenceId(referenceId)
                .expiresAt(Instant.now().plus(pointsExpiryDays, ChronoUnit.DAYS))
                .build();

        transactionRepository.save(txn);
        return txn;
    }

    /**
     * Refunds redeemed points (for cancelled orders).
     */
    @Transactional
    public void refundPoints(String userId, String orderId) {
        log.info("Refunding points for cancelled order {} for user {}", orderId, userId);

        LoyaltyTransaction redeemTxn = transactionRepository
                .findByOrderIdAndType(orderId, TransactionType.REDEEM)
                .orElse(null);

        if (redeemTxn == null) {
            log.info("No points to refund for order {}", orderId);
            return;
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int pointsToRefund = Math.abs(redeemTxn.getPoints());

        LoyaltyInfo loyalty = user.getLoyalty();
        loyalty.setPointsBalance(loyalty.getPointsBalance() + pointsToRefund);
        user.setLoyalty(loyalty);
        userRepository.save(user);

        LoyaltyTransaction refundTxn = LoyaltyTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .userId(userId)
                .type(TransactionType.REFUND)
                .points(pointsToRefund)
                .balanceAfter(loyalty.getPointsBalance())
                .description("Points refunded for cancelled order")
                .orderId(orderId)
                .build();

        transactionRepository.save(refundTxn);
    }

    /**
     * Calculates potential points for an order amount.
     */
    public int calculatePotentialPoints(String userId, BigDecimal orderAmount) {
        User user = userRepository.findByUserId(userId).orElse(null);
        double multiplier = 1.0;

        if (user != null && user.getLoyalty() != null) {
            multiplier = user.getLoyalty().getEarnMultiplier();
        }

        return (int) (orderAmount.intValue() * pointsPerRupee * multiplier);
    }

    private LoyaltyInfo initializeLoyalty(User user) {
        LoyaltyInfo loyalty = LoyaltyInfo.builder()
                .pointsBalance(0)
                .lifetimePoints(0)
                .tier(LoyaltyTier.BRONZE)
                .build();
        user.setLoyalty(loyalty);
        userRepository.save(user);
        return loyalty;
    }

    public record RedemptionResult(int pointsRedeemed, BigDecimal discountValue, int remainingBalance) {}
}

