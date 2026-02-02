package com.cateringmarketplace.module.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a payment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationRequest {
    private String orderId;
    private String bidId; // Added to support payment before order creation
    private String userId;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String paymentType;
    private String customerEmail;
    private String customerName;
    private String country;
}
