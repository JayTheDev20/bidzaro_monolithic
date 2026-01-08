package com.cateringmarketplace.module.promo.dto.response;

import com.cateringmarketplace.module.promo.model.PromoCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for promo code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromoCodeResponse {

    private String promoCodeId;
    private String code;
    private String title;
    private String description;
    private String type;
    private BigDecimal value;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private Instant validFrom;
    private Instant validTo;
    private Integer usageLimitGlobal;
    private Integer usageLimitPerUser;
    private Integer usedCount;
    private String applicableTo;
    private List<String> applicableVendorIds;
    private List<String> applicableCuisines;
    private Boolean firstOrderOnly;
    private String status;
    private Instant createdAt;

    public static PromoCodeResponse fromEntity(PromoCode promo) {
        if (promo == null) return null;

        return PromoCodeResponse.builder()
                .promoCodeId(promo.getPromoCodeId())
                .code(promo.getCode())
                .title(promo.getTitle())
                .description(promo.getDescription())
                .type(promo.getType() != null ? promo.getType().name() : null)
                .value(promo.getValue())
                .maxDiscountAmount(promo.getMaxDiscountAmount())
                .minOrderAmount(promo.getMinOrderAmount())
                .validFrom(promo.getValidFrom())
                .validTo(promo.getValidTo())
                .usageLimitGlobal(promo.getUsageLimitGlobal())
                .usageLimitPerUser(promo.getUsageLimitPerUser())
                .usedCount(promo.getUsedCount())
                .applicableTo(promo.getApplicableTo() != null ? promo.getApplicableTo().name() : null)
                .applicableVendorIds(promo.getApplicableVendorIds())
                .applicableCuisines(promo.getApplicableCuisines())
                .firstOrderOnly(promo.getFirstOrderOnly())
                .status(promo.getStatus() != null ? promo.getStatus().name() : null)
                .createdAt(promo.getCreatedAt())
                .build();
    }
}

