package com.cateringmarketplace.module.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating platform configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlatformConfigRequest {

    private BiddingConfigDTO biddingConfig;
    private PaymentConfigDTO paymentConfig;
    private CancellationPolicyDTO cancellationPolicy;
    private CommissionConfigDTO commissionConfig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BiddingConfigDTO {
        private Integer competitivePeriodHours;
        private Integer coolingPeriodHours;
        private Integer paymentCoolingPeriodHours;
        private Integer bidExpiryHours;
        private Integer minVendorsForCompetitive;
        private Integer maxBidRevisions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfigDTO {
        private BigDecimal tokenPercentage;
        private List<String> enabledGateways;
        private String defaultGateway;
        private Integer paymentTimeoutHours;
        private Boolean autoRefundEnabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancellationPolicyDTO {
        private Integer cancellationWindowDays;
        private List<RefundTierDTO> refundTiers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundTierDTO {
        private Integer daysBeforeEvent;
        private BigDecimal refundPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommissionConfigDTO {
        private BigDecimal platformFeePercentage;
        private BigDecimal vendorCommissionPercentage;
        private BigDecimal paymentGatewayFeePercentage;
    }
}

