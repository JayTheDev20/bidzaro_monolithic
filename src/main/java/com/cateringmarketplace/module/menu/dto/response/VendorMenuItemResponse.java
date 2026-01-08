package com.cateringmarketplace.module.menu.dto.response;

import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for vendor menu item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorMenuItemResponse {

    private String vendorItemId;
    private String vendorId;
    private String masterItemId;
    private String customName;
    private String customDescription;
    private PricingResponse pricing;
    private AvailabilityResponse availability;
    private Integer preparationTimeMinutes;
    private List<CustomizationOptionResponse> customizationOptions;
    private StatsResponse stats;
    private String status;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingResponse {
        private String currency;
        private BigDecimal pricePerPlate;
        private Integer minimumOrderQuantity;
        private BigDecimal discountPercentage;
        private BigDecimal discountedPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityResponse {
        private Boolean isAvailable;
        private String unavailableReason;
        private Instant unavailableUntil;
        private Integer advanceNoticeHours;
        private Integer maxDailyCapacity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationOptionResponse {
        private String optionName;
        private List<String> choices;
        private BigDecimal additionalCost;
        private Boolean isRequired;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsResponse {
        private Integer totalOrders;
        private BigDecimal averageRating;
        private Integer totalReviews;
    }

    public static VendorMenuItemResponse fromEntity(VendorMenuItem item) {
        if (item == null) return null;

        VendorMenuItemResponseBuilder builder = VendorMenuItemResponse.builder()
                .vendorItemId(item.getVendorItemId())
                .vendorId(item.getVendorId())
                .masterItemId(item.getMasterItemId())
                .customName(item.getCustomName())
                .customDescription(item.getCustomDescription())
                .preparationTimeMinutes(item.getPreparationTimeMinutes())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .createdAt(item.getCreatedAt());

        if (item.getPricing() != null) {
            builder.pricing(PricingResponse.builder()
                    .currency(item.getPricing().getCurrency())
                    .pricePerPlate(item.getPricing().getPricePerPlate())
                    .minimumOrderQuantity(item.getPricing().getMinimumOrderQuantity())
                    .discountPercentage(item.getPricing().getDiscountPercentage())
                    .discountedPrice(item.getPricing().getDiscountedPrice())
                    .build());
        }

        if (item.getAvailability() != null) {
            builder.availability(AvailabilityResponse.builder()
                    .isAvailable(item.getAvailability().getIsAvailable())
                    .unavailableReason(item.getAvailability().getUnavailableReason())
                    .unavailableUntil(item.getAvailability().getUnavailableUntil())
                    .advanceNoticeHours(item.getAvailability().getAdvanceNoticeHours())
                    .maxDailyCapacity(item.getAvailability().getMaxDailyCapacity())
                    .build());
        }

        if (item.getCustomizationOptions() != null) {
            builder.customizationOptions(item.getCustomizationOptions().stream()
                    .map(opt -> CustomizationOptionResponse.builder()
                            .optionName(opt.getOptionName())
                            .choices(opt.getChoices())
                            .additionalCost(opt.getAdditionalCost())
                            .isRequired(opt.getIsRequired())
                            .build())
                    .toList());
        }

        if (item.getStats() != null) {
            builder.stats(StatsResponse.builder()
                    .totalOrders(item.getStats().getTotalOrders())
                    .averageRating(item.getStats().getAverageRating())
                    .totalReviews(item.getStats().getTotalReviews())
                    .build());
        }

        return builder.build();
    }
}

