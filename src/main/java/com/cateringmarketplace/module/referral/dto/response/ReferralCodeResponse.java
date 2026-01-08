package com.cateringmarketplace.module.referral.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.cateringmarketplace.module.referral.model.ReferralCode;

/**
 * Response DTO for referral code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralCodeResponse {

    private String code;
    private String shareLink;
    private String status;
    private Integer totalReferrals;
    private Integer successfulReferrals;
    private Integer pendingReferrals;
    private Integer totalRewardsEarned;

    // =========================================================
    // MAPPER
    // =========================================================

    public static ReferralCodeResponse fromEntity(
            ReferralCode code,
            String baseUrl) {

        if (code == null) return null;

        String shareLink = baseUrl + "/register?ref=" + code.getCode();

        return ReferralCodeResponse.builder()
                .code(code.getCode())
                .shareLink(shareLink)
                .status(code.getStatus() != null
                        ? code.getStatus().name()
                        : null)
                .totalReferrals(code.getTotalReferrals())
                .successfulReferrals(code.getSuccessfulReferrals())
                .pendingReferrals(
                        code.getTotalReferrals()
                                - code.getSuccessfulReferrals())
                .totalRewardsEarned(code.getTotalRewardsEarned())
                .build();
    }
}
