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

import java.time.Instant;

/**
 * WishlistItem entity for user wishlists.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlist_items")
@CompoundIndex(name = "user_master_item_idx", def = "{'user_id': 1, 'master_item_id': 1}", unique = true)
public class WishlistItem {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("wishlist_item_id")
    private String wishlistItemId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("master_item_id")
    private String masterItemId;

    @Field("item_name")
    private String itemName;

    @Field("description")
    private String description;

    @Field("category_id")
    private String categoryId;

    @Field("cuisine_type")
    private String cuisineType;

    @Field("food_type")
    private String foodType;

    @Field("image_url")
    private String imageUrl;

    @CreatedDate
    @Field("added_at")
    private Instant addedAt;
}
