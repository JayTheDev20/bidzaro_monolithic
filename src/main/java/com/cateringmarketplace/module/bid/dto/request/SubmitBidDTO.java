package com.cateringmarketplace.module.bid.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for submitting a vendor bid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitBidDTO {

    @NotNull(message = "Quoted price is required")
    private QuotedPriceDTO quotedPrice;

    private List<ItemizedPriceDTO> itemizedPricing;

    private DeliveryDetailsDTO deliveryDetails;

    private StaffProvidedDTO staffProvided;

    private String termsAndConditions;

    private Integer validityPeriodHours;

    @DecimalMin(value = "0.0", message = "Advance percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Advance percentage cannot exceed 100")
    private BigDecimal advancePercentage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotedPriceDTO {
        private String currency;

        @NotNull(message = "Subtotal is required")
        @DecimalMin(value = "0.0", message = "Subtotal must be positive")
        private BigDecimal subtotal;

        private BigDecimal serviceCharge;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", message = "Total amount must be positive")
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemizedPriceDTO {
        private String vendorItemId;
        private String itemName;
        @Min(value = 1)
        private Integer quantity;
        @DecimalMin(value = "0.0")
        private BigDecimal pricePerPlate;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryDetailsDTO {
        private String estimatedSetupTime;
        private String foodReadyTime;
        private String cleanupTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffProvidedDTO {
        private Integer chefs;
        private Integer servers;
        private Integer cleaners;
    }
}
