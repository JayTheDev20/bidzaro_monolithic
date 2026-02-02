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

import java.time.Instant;

/**
 * DraftCartItem entity for storing master menu items before vendor selection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "draft_cart_items")
@CompoundIndex(name = "user_master_item_idx", def = "{'user_id': 1, 'master_item_id': 1}", unique = true)
public class DraftCartItem {

    @Id
    private String id;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed
    @Field("master_item_id")
    private String masterItemId;

    @Field("item_name")
    private String itemName;

    private Integer quantity;

    @CreatedDate
    @Field("added_at")
    private Instant addedAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Indexed(expireAfterSeconds = 2592000) // 30 days TTL
    @Field("expires_at")
    private Instant expiresAt;
}
