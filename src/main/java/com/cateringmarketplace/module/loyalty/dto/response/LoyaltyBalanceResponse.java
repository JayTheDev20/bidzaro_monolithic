package com.cateringmarketplace.module.loyalty.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.cateringmarketplace.module.loyalty.model.LoyaltyInfo;
import com.cateringmarketplace.module.loyalty.model.LoyaltyInfo.LoyaltyTier;

/**
 * Response DTO for loyalty balance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyBalanceResponse {

    private Integer pointsBalance;
    private Integer lifetimePoints;
    private String tier;
    private Integer pointsToNextTier;
    private String nextTier;
    private BigDecimal pointsValue; // Monetary value of points
    private Double earnMultiplier;

    // =========================================================
    // FACTORY
    // =========================================================

    public static LoyaltyBalanceResponse fromLoyaltyInfo(
            LoyaltyInfo info,
            BigDecimal pointsToRupeeRatio) {

        if (info == null) {
            return LoyaltyBalanceResponse.builder()
                    .pointsBalance(0)
                    .lifetimePoints(0)
                    .tier(LoyaltyTier.BRONZE.name())
                    .earnMultiplier(1.0)
                    .pointsValue(BigDecimal.ZERO)
                    .build();
        }

        Integer pointsToNext = calculatePointsToNextTier(info);
        String nextTierName = getNextTier(info.getTier());

        BigDecimal value = BigDecimal.valueOf(info.getPointsBalance())
                .multiply(pointsToRupeeRatio);

        return LoyaltyBalanceResponse.builder()
                .pointsBalance(info.getPointsBalance())
                .lifetimePoints(info.getLifetimePoints())
                .tier(info.getTier() != null
                        ? info.getTier().name()
                        : LoyaltyTier.BRONZE.name())
                .pointsToNextTier(pointsToNext)
                .nextTier(nextTierName)
                .pointsValue(value)
                .earnMultiplier(info.getEarnMultiplier())
                .build();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private static Integer calculatePointsToNextTier(LoyaltyInfo info) {

        if (info.getTier() == null) return 1000;

        return switch (info.getTier()) {
            case BRONZE -> 1000 - info.getLifetimePoints();
            case SILVER -> 5000 - info.getLifetimePoints();
            case GOLD -> 10000 - info.getLifetimePoints();
            case PLATINUM -> 0; // Already at max tier
        };
    }

    private static String getNextTier(LoyaltyTier current) {

        if (current == null) return LoyaltyTier.SILVER.name();

        return switch (current) {
            case BRONZE -> LoyaltyTier.SILVER.name();
            case SILVER -> LoyaltyTier.GOLD.name();
            case GOLD -> LoyaltyTier.PLATINUM.name();
            case PLATINUM -> null;
        };
    }
}
