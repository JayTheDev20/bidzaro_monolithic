package com.cateringmarketplace.module.admin.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Platform configuration entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "platform_config")
public class PlatformConfig {

    // =========================================================
    // ROOT FIELDS
    // =========================================================

    @Id
    private String id;

    @Field("config_id")
    @Indexed(unique = true)
    private String configId;

    @Indexed(unique = true)
    private String country;

    @Field("bidding_config")
    private BiddingConfig biddingConfig;

    @Field("payment_config")
    private PaymentConfig paymentConfig;

    @Field("cancellation_policy")
    private CancellationPolicy cancellationPolicy;

    @Field("commission_config")
    private CommissionConfig commissionConfig;

    @Field("updated_by")
    private String updatedBy;

    @Field("updated_at")
    private Instant updatedAt;

    // =========================================================
    // FACTORY
    // =========================================================

    /**
     * Creates a default platform configuration.
     */
    public static PlatformConfig createDefault(String country) {

        return PlatformConfig.builder()
                .country(country)
                .biddingConfig(BiddingConfig.builder().build())
                .paymentConfig(PaymentConfig.builder().build())
                .cancellationPolicy(
                        CancellationPolicy.builder()
                                .refundTiers(List.of(
                                        RefundTier.builder()
                                                .daysBeforeEvent(30)
                                                .refundPercentage(new BigDecimal("100"))
                                                .build(),
                                        RefundTier.builder()
                                                .daysBeforeEvent(15)
                                                .refundPercentage(new BigDecimal("75"))
                                                .build(),
                                        RefundTier.builder()
                                                .daysBeforeEvent(7)
                                                .refundPercentage(new BigDecimal("50"))
                                                .build(),
                                        RefundTier.builder()
                                                .daysBeforeEvent(3)
                                                .refundPercentage(new BigDecimal("25"))
                                                .build(),
                                        RefundTier.builder()
                                                .daysBeforeEvent(0)
                                                .refundPercentage(BigDecimal.ZERO)
                                                .build()
                                ))
                                .build()
                )
                .commissionConfig(CommissionConfig.builder().build())
                .updatedAt(Instant.now())
                .build();
    }

    // =========================================================
    // BIDDING CONFIG
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BiddingConfig {

        @Builder.Default
        @Field("competitive_period_hours")
        private Integer competitivePeriodHours = 72;

        @Builder.Default
        @Field("cooling_period_hours")
        private Integer coolingPeriodHours = 24;

        @Builder.Default
        @Field("payment_cooling_period_hours")
        private Integer paymentCoolingPeriodHours = 2;

        @Builder.Default
        @Field("bid_expiry_hours")
        private Integer bidExpiryHours = 168;

        @Builder.Default
        @Field("min_vendors_for_competitive")
        private Integer minVendorsForCompetitive = 3;

        @Builder.Default
        @Field("max_bid_revisions")
        private Integer maxBidRevisions = 5;
    }

    // =========================================================
    // PAYMENT CONFIG
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfig {

        @Builder.Default
        @Field("token_percentage")
        private BigDecimal tokenPercentage = new BigDecimal("25");

        @Builder.Default
        @Field("enabled_gateways")
        private List<String> enabledGateways =
                new ArrayList<>(List.of("RAZORPAY"));

        @Builder.Default
        @Field("default_gateway")
        private String defaultGateway = "RAZORPAY";

        @Builder.Default
        @Field("payment_timeout_hours")
        private Integer paymentTimeoutHours = 24;

        @Builder.Default
        @Field("auto_refund_enabled")
        private Boolean autoRefundEnabled = true;
    }

    // =========================================================
    // CANCELLATION POLICY
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancellationPolicy {

        @Builder.Default
        @Field("cancellation_window_days")
        private Integer cancellationWindowDays = 30;

        @Builder.Default
        @Field("refund_tiers")
        private List<RefundTier> refundTiers = new ArrayList<>();
    }

    // =========================================================
    // REFUND TIER
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundTier {

        @Field("days_before_event")
        private Integer daysBeforeEvent;

        @Field("refund_percentage")
        private BigDecimal refundPercentage;
    }

    // =========================================================
    // COMMISSION CONFIG
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommissionConfig {

        @Builder.Default
        @Field("platform_fee_percentage")
        private BigDecimal platformFeePercentage = new BigDecimal("2");

        @Builder.Default
        @Field("vendor_commission_percentage")
        private BigDecimal vendorCommissionPercentage = new BigDecimal("10");

        @Builder.Default
        @Field("payment_gateway_fee_percentage")
        private BigDecimal paymentGatewayFeePercentage = new BigDecimal("2");
    }
}
