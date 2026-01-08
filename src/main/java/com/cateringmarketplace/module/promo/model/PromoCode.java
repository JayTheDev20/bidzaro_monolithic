package com.cateringmarketplace.module.promo.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PromoCode entity for discounts and promotions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promo_codes")
public class PromoCode {

    // =========================================================
    // IDS
    // =========================================================

    @Id
    private String id;

    @Field("promo_code_id")
    @Indexed(unique = true)
    private String promoCodeId;

    @Indexed(unique = true)
    private String code;

    // =========================================================
    // BASIC INFO
    // =========================================================

    private String title;
    private String description;

    private PromoType type; // PERCENTAGE, FLAT

    private BigDecimal value; // percentage or flat amount

    // =========================================================
    // LIMITS
    // =========================================================

    @Field("max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Field("min_order_amount")
    private BigDecimal minOrderAmount;

    @Field("usage_limit_global")
    private Integer usageLimitGlobal;

    @Builder.Default
    @Field("usage_limit_per_user")
    private Integer usageLimitPerUser = 1;

    @Builder.Default
    @Field("used_count")
    private Integer usedCount = 0;

    // =========================================================
    // VALIDITY
    // =========================================================

    @Field("valid_from")
    private Instant validFrom;

    @Field("valid_to")
    private Instant validTo;

    // =========================================================
    // APPLICABILITY
    // =========================================================

    @Field("applicable_to")
    private ApplicableTo applicableTo;

    @Builder.Default
    @Field("applicable_vendor_ids")
    private List<String> applicableVendorIds = new ArrayList<>();

    @Builder.Default
    @Field("applicable_user_ids")
    private List<String> applicableUserIds = new ArrayList<>();

    @Builder.Default
    @Field("applicable_cuisines")
    private List<String> applicableCuisines = new ArrayList<>();

    // =========================================================
    // FLAGS
    // =========================================================

    @Builder.Default
    @Field("first_order_only")
    private Boolean firstOrderOnly = false;

    @Indexed
    private PromoStatus status;

    // =========================================================
    // AUDIT
    // =========================================================

    @Field("created_by")
    private String createdBy;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // =========================================================
    // ENUMS
    // =========================================================

    public enum PromoType {
        PERCENTAGE,
        FLAT
    }

    public enum ApplicableTo {
        ALL,
        SPECIFIC_VENDORS,
        SPECIFIC_USERS,
        SPECIFIC_CUISINES
    }

    public enum PromoStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }

    // =========================================================
    // BUSINESS LOGIC
    // =========================================================

    /**
     * Checks if promo is currently valid.
     */
    public boolean isValid() {

        if (status != PromoStatus.ACTIVE) return false;

        Instant now = Instant.now();

        if (validFrom != null && now.isBefore(validFrom)) return false;
        if (validTo != null && now.isAfter(validTo)) return false;
        if (usageLimitGlobal != null && usedCount >= usageLimitGlobal) return false;

        return true;
    }

    /**
     * Calculates discount amount for given order total.
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {

        if (!isValid()) return BigDecimal.ZERO;
        if (minOrderAmount != null && orderTotal.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;

        if (type == PromoType.PERCENTAGE) {
            discount = orderTotal.multiply(value)
                    .divide(new BigDecimal("100"));
        } else {
            discount = value;
        }

        // Apply max cap
        if (maxDiscountAmount != null &&
                discount.compareTo(maxDiscountAmount) > 0) {
            discount = maxDiscountAmount;
        }

        // Discount cannot exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }

        return discount;
    }
}
