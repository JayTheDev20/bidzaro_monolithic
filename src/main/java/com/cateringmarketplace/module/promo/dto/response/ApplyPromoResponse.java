package com.cateringmarketplace.module.promo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for promo application result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPromoResponse {

    private boolean valid;
    private String promoCodeId;
    private String code;
    private BigDecimal orderTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String message;
    private String errorCode;
}

