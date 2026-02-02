package com.cateringmarketplace.module.cart.model;

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
 * Represents an item selected from the Master Menu, before a vendor is chosen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "master_cart_items")
@CompoundIndex(name = "user_master_item_idx", def = "{'user_id': 1, 'master_item_id': 1}", unique = true)
public class MasterCartItem {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("master_item_id")
    private String masterItemId;

    @Field("item_name")
    private String itemName;

    @Field("image_url")
    private String imageUrl;

    private Integer quantity;

    @CreatedDate
    @Field("added_at")
    private Instant addedAt;
}
