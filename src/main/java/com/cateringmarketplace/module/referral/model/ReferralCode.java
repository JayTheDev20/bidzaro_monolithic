package com.cateringmarketplace.module.referral.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReferralCode entity for user referral codes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "referral_codes")
public class ReferralCode {

    // =========================================================
    // IDS
    // =========================================================

    @Id
    private String id;

    @Field("code_id")
    @Indexed(unique = true)
    private String codeId;

    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    @Indexed(unique = true)
    private String code;

    // =========================================================
    // STATS
    // =========================================================

    @Builder.Default
    @Field("total_referrals")
    private Integer totalReferrals = 0;

    @Builder.Default
    @Field("successful_referrals")
    private Integer successfulReferrals = 0;

    @Builder.Default
    @Field("total_rewards_earned")
    private Integer totalRewardsEarned = 0;

    // =========================================================
    // STATUS
    // =========================================================

    private ReferralStatus status;

    // =========================================================
    // AUDIT
    // =========================================================

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // =========================================================
    // ENUM
    // =========================================================

    public enum ReferralStatus {
        ACTIVE,
        INACTIVE
    }

    // =========================================================
    // UTILITY
    // =========================================================

    /**
     * Generates a referral code from user ID.
     */
    public static String generateCode(String userId) {
        String base = userId.replaceAll("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "REF" + base;
    }
}
