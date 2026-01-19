package com.cateringmarketplace.module.menu.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating vendor menu item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorMenuItemRequest {

    @NotBlank(message = "Master item ID is required")
    private String masterItemId;

    private String customName;
    private String customDescription;

    @NotNull(message = "Price per plate is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal pricePerPlate;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQuantity;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    private BigDecimal discountPercentage;

    private Boolean isAvailable;
    private String unavailableReason;

    @Min(value = 0, message = "Advance notice must be non-negative")
    private Integer advanceNoticeHours;

    @Min(value = 1, message = "Max daily capacity must be at least 1")
    private Integer maxDailyCapacity;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTimeMinutes;

    @Valid
    private List<CustomizationOptionDTO> customizationOptions;

    // =========================================================
    // Nested DTO
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationOptionDTO {

        @NotBlank(message = "Option name is required")
        private String optionName;

        @NotEmpty(message = "Choices list cannot be empty")
        private List<String> choices;

        @DecimalMin(value = "0.0", message = "Additional cost must be non-negative")
        private BigDecimal additionalCost;

        private Boolean isRequired;
    }
}
