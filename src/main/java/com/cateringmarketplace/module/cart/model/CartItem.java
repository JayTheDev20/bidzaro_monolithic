package com.cateringmarketplace.module.cart.model;

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
 * CartItem entity for shopping cart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cart_items")
@CompoundIndex(name = "user_vendor_item_idx", def = "{'user_id': 1, 'vendor_id': 1, 'vendor_item_id': 1}", unique = true)
public class CartItem {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("cart_item_id")
    private String cartItemId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed
    @Field("vendor_id")
    private String vendorId;

    @Field("vendor_item_id")
    private String vendorItemId;

    @Field("item_name")
    private String itemName;

    private Integer quantity;

    @Field("price_per_plate")
    private BigDecimal pricePerPlate;

    @Field("total_price")
    private BigDecimal totalPrice;

    @Builder.Default
    private List<Customization> customizations = new ArrayList<>();

    @CreatedDate
    @Field("added_at")
    private Instant addedAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Indexed(expireAfterSeconds = 2592000) // 30 days TTL
    @Field("expires_at")
    private Instant expiresAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customization {
        @Field("option_name")
        private String optionName;
        @Field("selected_choice")
        private String selectedChoice;
    }

    /**
     * Calculates total price.
     */
    public void calculateTotalPrice() {
        if (pricePerPlate != null && quantity != null) {
            this.totalPrice = pricePerPlate.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Updates quantity and recalculates total.
     */
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        calculateTotalPrice();
    }
}
