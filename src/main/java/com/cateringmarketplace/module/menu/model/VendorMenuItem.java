package com.cateringmarketplace.module.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * VendorMenuItem entity representing vendor-specific menu items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vendor_menu_items")
@CompoundIndex(name = "vendor_item_idx", def = "{'vendor_id': 1, 'master_item_id': 1}", unique = true)
public class VendorMenuItem {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("vendor_item_id")
    private String vendorItemId;

    @Indexed
    @Field("vendor_id")
    private String vendorId;

    @Indexed
    @Field("master_item_id")
    private String masterItemId;

    @Field("custom_name")
    private String customName;

    @Field("custom_description")
    private String customDescription;

    private VendorItemPricing pricing;

    private VendorItemAvailability availability;

    @Field("preparation_time_minutes")
    private Integer preparationTimeMinutes;

    @Field("customization_options")
    @Builder.Default
    private List<CustomizationOption> customizationOptions = new ArrayList<>();

    private VendorItemStats stats;

    private VendorItemStatus status;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // Enums
    public enum VendorItemStatus {
        ACTIVE,
        INACTIVE,
        OUT_OF_STOCK
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorItemPricing {
        @Builder.Default
        private String currency = "USD";
        private BigDecimal pricePerPlate;
        private Integer minimumOrderQuantity;
        private BigDecimal discountPercentage;
        private BigDecimal discountedPrice;

        /**
         * Calculates effective price considering discount.
         */
        public BigDecimal getEffectivePrice() {
            if (discountedPrice != null && discountedPrice.compareTo(BigDecimal.ZERO) > 0) {
                return discountedPrice;
            }
            if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = pricePerPlate.multiply(discountPercentage).divide(new BigDecimal("100"));
                return pricePerPlate.subtract(discount);
            }
            return pricePerPlate;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorItemAvailability {
        @Builder.Default
        private Boolean isAvailable = true;
        private String unavailableReason;
        private Instant unavailableUntil;
        private Integer advanceNoticeHours;
        private Integer maxDailyCapacity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationOption {
        private String optionName;
        @Builder.Default
        private List<String> choices = new ArrayList<>();
        private BigDecimal additionalCost;
        @Builder.Default
        private Boolean isRequired = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorItemStats {
        @Builder.Default
        private Integer totalOrders = 0;
        @Builder.Default
        private BigDecimal averageRating = BigDecimal.ZERO;
        @Builder.Default
        private Integer totalReviews = 0;
    }

    /**
     * Checks if item is available.
     */
    public boolean isAvailable() {
        if (status != VendorItemStatus.ACTIVE) return false;
        if (availability == null) return true;
        if (!Boolean.TRUE.equals(availability.getIsAvailable())) return false;
        if (availability.getUnavailableUntil() != null &&
            Instant.now().isBefore(availability.getUnavailableUntil())) {
            return false;
        }
        return true;
    }

    /**
     * Gets effective price.
     */
    public BigDecimal getEffectivePrice() {
        return pricing != null ? pricing.getEffectivePrice() : BigDecimal.ZERO;
    }
}

