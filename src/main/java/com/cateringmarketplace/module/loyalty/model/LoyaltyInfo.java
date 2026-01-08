package com.cateringmarketplace.module.loyalty.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Loyalty info embedded in User document.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyInfo {

    @Field("points_balance")
    @Builder.Default
    private Integer pointsBalance = 0;

    @Field("lifetime_points")
    @Builder.Default
    private Integer lifetimePoints = 0;

    private LoyaltyTier tier;

    @Field("tier_updated_at")
    private java.time.Instant tierUpdatedAt;

    public enum LoyaltyTier {
        BRONZE,     // 0 - 999 lifetime points
        SILVER,     // 1000 - 4999 lifetime points
        GOLD,       // 5000 - 9999 lifetime points
        PLATINUM    // 10000+ lifetime points
    }

    /**
     * Updates tier based on lifetime points.
     */
    public void updateTier() {
        LoyaltyTier newTier;
        if (lifetimePoints >= 10000) {
            newTier = LoyaltyTier.PLATINUM;
        } else if (lifetimePoints >= 5000) {
            newTier = LoyaltyTier.GOLD;
        } else if (lifetimePoints >= 1000) {
            newTier = LoyaltyTier.SILVER;
        } else {
            newTier = LoyaltyTier.BRONZE;
        }

        if (tier != newTier) {
            tier = newTier;
            tierUpdatedAt = java.time.Instant.now();
        }
    }

    /**
     * Gets earn rate multiplier based on tier.
     */
    public double getEarnMultiplier() {
        return switch (tier) {
            case PLATINUM -> 2.0;
            case GOLD -> 1.5;
            case SILVER -> 1.25;
            default -> 1.0;
        };
    }
}

