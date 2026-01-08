package com.cateringmarketplace.module.referral.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for referral statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralStatsResponse {

    private String referralCode;
    private Integer totalInvites;
    private Integer signups;
    private Integer completedOrders;
    private Integer pendingRewards;
    private Integer grantedRewards;
    private Integer totalPointsEarned;
    private List<ReferralDetail> recentReferrals;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferralDetail {
        private String referredUserName;
        private String eventType;
        private String rewardStatus;
        private Integer rewardPoints;
        private String createdAt;
    }
}

