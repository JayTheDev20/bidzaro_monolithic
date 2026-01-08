package com.cateringmarketplace.module.promo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * PromoRedemption entity for tracking promo code usage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promo_redemptions")
@CompoundIndex(name = "user_promo_idx", def = "{'user_id': 1, 'promo_code_id': 1}")
public class PromoRedemption {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("redemption_id")
    private String redemptionId;

    @Indexed
    @Field("promo_code_id")
    private String promoCodeId;

    @Field("promo_code")
    private String promoCode;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed
    @Field("order_id")
    private String orderId;

    @Field("order_amount")
    private BigDecimal orderAmount;

    @Field("discount_amount")
    private BigDecimal discountAmount;

    @Field("final_amount")
    private BigDecimal finalAmount;

    @CreatedDate
    @Field("redeemed_at")
    private Instant redeemedAt;
}

