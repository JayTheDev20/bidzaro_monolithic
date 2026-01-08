package com.cateringmarketplace.module.promo.dto.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating promo codes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromoRequest {

    @NotBlank(message = "Promo code is required")
    private String code;

    private String title;

    private String description;

    // PERCENTAGE, FLAT
    @NotNull(message = "Promo type is required")
    private String type;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minOrderAmount;

    private Instant validFrom;

    private Instant validTo;

    private Integer usageLimitGlobal;

    private Integer usageLimitPerUser;

    // ALL, SPECIFIC_VENDORS, SPECIFIC_USERS, SPECIFIC_CUISINES
    private String applicableTo;

    private List<String> applicableVendorIds;

    private List<String> applicableUserIds;

    private List<String> applicableCuisines;

    private Boolean firstOrderOnly;
}
