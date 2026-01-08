package com.cateringmarketplace.module.promo.model;
}
    }
        return discount;

        }
            discount = orderTotal;
        if (discount.compareTo(orderTotal) > 0) {
        // Discount cannot exceed order total

        }
            discount = maxDiscountAmount;
        if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
        // Apply max cap

        }
            discount = value;
        } else {
            discount = orderTotal.multiply(value).divide(new BigDecimal("100"));
        if (type == PromoType.PERCENTAGE) {
        BigDecimal discount;

        if (minOrderAmount != null && orderTotal.compareTo(minOrderAmount) < 0) return BigDecimal.ZERO;
        if (!isValid()) return BigDecimal.ZERO;
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
     */
     * Calculates discount amount for given order total.
    /**

    }
        return true;

        if (usageLimitGlobal != null && usedCount >= usageLimitGlobal) return false;
        if (validTo != null && now.isAfter(validTo)) return false;
        if (validFrom != null && now.isBefore(validFrom)) return false;
        Instant now = Instant.now();

        if (status != PromoStatus.ACTIVE) return false;
    public boolean isValid() {
     */
     * Checks if promo is currently valid.
    /**

    }
        EXPIRED
        INACTIVE,
        ACTIVE,
    public enum PromoStatus {

    }
        SPECIFIC_CUISINES
        SPECIFIC_USERS,
        SPECIFIC_VENDORS,
        ALL,
    public enum ApplicableTo {

    }
        FLAT
        PERCENTAGE,
    public enum PromoType {

    private Instant updatedAt;
    @Field("updated_at")
    @LastModifiedDate

    private Instant createdAt;
    @Field("created_at")
    @CreatedDate

    private String createdBy;
    @Field("created_by")

    private PromoStatus status;
    @Indexed

    private Boolean firstOrderOnly = false;
    @Builder.Default
    @Field("first_order_only")

    private List<String> applicableCuisines = new ArrayList<>();
    @Builder.Default
    @Field("applicable_cuisines")

    private List<String> applicableUserIds = new ArrayList<>();
    @Builder.Default
    @Field("applicable_user_ids")

    private List<String> applicableVendorIds = new ArrayList<>();
    @Builder.Default
    @Field("applicable_vendor_ids")

    private ApplicableTo applicableTo;
    @Field("applicable_to")

    private Integer usedCount = 0;
    @Builder.Default
    @Field("used_count")

    private Integer usageLimitPerUser = 1;
    @Builder.Default
    @Field("usage_limit_per_user")

    private Integer usageLimitGlobal;
    @Field("usage_limit_global")

    private Instant validTo;
    @Field("valid_to")

    private Instant validFrom;
    @Field("valid_from")

    private BigDecimal minOrderAmount;
    @Field("min_order_amount")

    private BigDecimal maxDiscountAmount;
    @Field("max_discount_amount")

    private BigDecimal value; // Percentage or flat amount

    private PromoType type;

    private String description;

    private String title;

    private String code;
    @Indexed(unique = true)

    private String promoCodeId;
    @Field("promo_code_id")
    @Indexed(unique = true)

    private String id;
    @Id

public class PromoCode {
@Document(collection = "promo_codes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
 */
 * PromoCode entity for discounts and promotions.
/**

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;


