package com.cateringmarketplace.module.loyalty.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoyaltyTransaction entity for tracking loyalty points.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loyalty_transactions")
public class LoyaltyTransaction {

    // =========================================================
    // IDS
    // =========================================================

    @Id
    private String id;

    @Field("transaction_id")
    @Indexed(unique = true)
    private String transactionId;

    @Field("user_id")
    @Indexed
    private String userId;

    // =========================================================
    // TRANSACTION DETAILS
    // =========================================================

    private TransactionType type;   // EARN, REDEEM, ADJUST, etc.

    private Integer points;

    @Field("balance_after")
    private Integer balanceAfter;

    private String description;

    // =========================================================
    // REFERENCES
    // =========================================================

    @Field("order_id")
    private String orderId;

    @Field("reference_id")
    private String referenceId;

    // =========================================================
    // VALIDITY
    // =========================================================

    @Field("expires_at")
    private Instant expiresAt;

    // =========================================================
    // AUDIT
    // =========================================================

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // =========================================================
    // ENUM
    // =========================================================

    public enum TransactionType {

        EARN,           // Points earned from order
        REDEEM,         // Points redeemed for discount
        ADJUST,         // Manual adjustment by admin
        EXPIRE,         // Points expired
        BONUS,          // Bonus points (signup, referral, etc.)
        REFUND          // Refund of redeemed points (order cancelled)
    }
}
