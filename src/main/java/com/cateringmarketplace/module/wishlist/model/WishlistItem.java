package com.cateringmarketplace.module.wishlist.model;

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
 * WishlistItem entity for user wishlists.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlist_items")
@CompoundIndex(name = "user_item_idx", def = "{'user_id': 1, 'vendor_item_id': 1}", unique = true)
public class WishlistItem {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("wishlist_item_id")
    private String wishlistItemId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("vendor_id")
    private String vendorId;

    @Field("vendor_item_id")
    private String vendorItemId;

    @Field("item_name")
    private String itemName;

    @Field("price_per_plate")
    private BigDecimal pricePerPlate;

    @Field("image_url")
    private String imageUrl;

    @CreatedDate
    @Field("added_at")
    private Instant addedAt;
}
