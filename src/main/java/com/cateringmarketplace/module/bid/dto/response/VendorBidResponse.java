package com.cateringmarketplace.module.bid.dto.response;

import com.cateringmarketplace.module.bid.dto.response.BidRequestResponse.EventDetailsResponse;
import com.cateringmarketplace.module.bid.model.VendorBid;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for vendor bid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorBidResponse {

    private String bidId;
    private String bidRequestId;
    private String vendorId;
    private String vendorName;
    private EventDetailsResponse eventDetails; // Added Event Details
    private QuotedPriceResponse quotedPrice;
    private List<ItemizedPriceResponse> itemizedPricing;
    private DeliveryDetailsResponse deliveryDetails;
    private StaffProvidedResponse staffProvided;
    private String termsAndConditions;
    private Integer validityPeriodHours;
    private BigDecimal advancePercentage;
    private BigDecimal requiredAdvanceAmount;
    private Integer revisionCount;
    private String status;
    private Boolean isLowest;
    private Integer rank;
    private Instant submittedAt;
    private Instant expiresAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotedPriceResponse {
        private String currency;
        private BigDecimal subtotal;
        private BigDecimal serviceCharge;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemizedPriceResponse {
        private String vendorItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal pricePerPlate;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryDetailsResponse {
        private String estimatedSetupTime;
        private String foodReadyTime;
        private String cleanupTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffProvidedResponse {
        private Integer chefs;
        private Integer servers;
        private Integer cleaners;
    }

    /**
     * Creates a VendorBidResponse from a VendorBid entity.
     */
    public static VendorBidResponse fromEntity(VendorBid bid) {
        if (bid == null) return null;

        VendorBidResponseBuilder builder = VendorBidResponse.builder()
                .bidId(bid.getBidId())
                .bidRequestId(bid.getBidRequestId())
                .vendorId(bid.getVendorId())
                .vendorName(bid.getVendorName())
                .termsAndConditions(bid.getTermsAndConditions())
                .validityPeriodHours(bid.getValidityPeriodHours())
                .advancePercentage(bid.getAdvancePercentage())
                .requiredAdvanceAmount(bid.getRequiredAdvanceAmount())
                .revisionCount(bid.getRevisionCount())
                .status(bid.getStatus() != null ? bid.getStatus().name() : null)
                .isLowest(bid.getIsLowest())
                .rank(bid.getRank())
                .submittedAt(bid.getSubmittedAt())
                .expiresAt(bid.getExpiresAt());

        // Map quoted price
        if (bid.getQuotedPrice() != null) {
            builder.quotedPrice(QuotedPriceResponse.builder()
                    .currency(bid.getQuotedPrice().getCurrency())
                    .subtotal(bid.getQuotedPrice().getSubtotal())
                    .serviceCharge(bid.getQuotedPrice().getServiceCharge())
                    .taxPercentage(bid.getQuotedPrice().getTaxPercentage())
                    .taxAmount(bid.getQuotedPrice().getTaxAmount())
                    .totalAmount(bid.getQuotedPrice().getTotalAmount())
                    .build());
        }

        // Map itemized pricing
        if (bid.getItemizedPricing() != null) {
            builder.itemizedPricing(bid.getItemizedPricing().stream()
                    .map(ip -> ItemizedPriceResponse.builder()
                            .vendorItemId(ip.getVendorItemId())
                            .itemName(ip.getItemName())
                            .quantity(ip.getQuantity())
                            .pricePerPlate(ip.getPricePerPlate())
                            .totalPrice(ip.getTotalPrice())
                            .build())
                    .toList());
        }

        // Map delivery details
        if (bid.getDeliveryDetails() != null) {
            builder.deliveryDetails(DeliveryDetailsResponse.builder()
                    .estimatedSetupTime(bid.getDeliveryDetails().getEstimatedSetupTime())
                    .foodReadyTime(bid.getDeliveryDetails().getFoodReadyTime())
                    .cleanupTime(bid.getDeliveryDetails().getCleanupTime())
                    .build());
        }

        // Map staff provided
        if (bid.getStaffProvided() != null) {
            builder.staffProvided(StaffProvidedResponse.builder()
                    .chefs(bid.getStaffProvided().getChefs())
                    .servers(bid.getStaffProvided().getServers())
                    .cleaners(bid.getStaffProvided().getCleaners())
                    .build());
        }

        return builder.build();
    }
}
