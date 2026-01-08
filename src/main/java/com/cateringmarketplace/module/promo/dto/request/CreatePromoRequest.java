package com.cateringmarketplace.module.promo.dto.request;
}
    private Boolean firstOrderOnly;

    private List<String> applicableCuisines;

    private List<String> applicableUserIds;

    private List<String> applicableVendorIds;

    private String applicableTo; // ALL, SPECIFIC_VENDORS, SPECIFIC_USERS, SPECIFIC_CUISINES

    private Integer usageLimitPerUser;

    private Integer usageLimitGlobal;

    private Instant validTo;

    private Instant validFrom;

    private BigDecimal minOrderAmount;

    private BigDecimal maxDiscountAmount;

    private BigDecimal value;
    @Positive(message = "Value must be positive")
    @NotNull(message = "Value is required")

    private String type; // PERCENTAGE, FLAT
    @NotNull(message = "Promo type is required")

    private String description;

    private String title;

    private String code;
    @NotBlank(message = "Promo code is required")

public class CreatePromoRequest {
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
 */
 * Request DTO for creating promo codes.
/**

import java.util.List;
import java.time.Instant;
import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;


