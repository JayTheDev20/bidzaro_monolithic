package com.cateringmarketplace.module.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for payment initiation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponse {
    private String transactionId;
    private String gatewayOrderId;
    private String gatewayName;
    private BigDecimal amount;
    private String currency;
    private String keyId; // For Razorpay
    private String clientSecret; // For Stripe
    private String publishableKey; // For Stripe
}

