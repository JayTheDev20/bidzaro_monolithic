package com.cateringmarketplace.module.loyalty.dto.response;

import com.cateringmarketplace.module.loyalty.model.LoyaltyTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for loyalty transaction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyTransactionResponse {

    private String transactionId;
    private String type;
    private Integer points;
    private Integer balanceAfter;
    private String description;
    private String orderId;
    private Instant createdAt;

    public static LoyaltyTransactionResponse fromEntity(LoyaltyTransaction txn) {
        if (txn == null) return null;

        return LoyaltyTransactionResponse.builder()
                .transactionId(txn.getTransactionId())
                .type(txn.getType() != null ? txn.getType().name() : null)
                .points(txn.getPoints())
                .balanceAfter(txn.getBalanceAfter())
                .description(txn.getDescription())
                .orderId(txn.getOrderId())
                .createdAt(txn.getCreatedAt())
                .build();
    }
}

