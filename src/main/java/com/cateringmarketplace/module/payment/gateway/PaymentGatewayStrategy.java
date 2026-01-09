package com.cateringmarketplace.module.payment.gateway;

import com.cateringmarketplace.module.payment.dto.PaymentInitiationRequest;
import com.cateringmarketplace.module.payment.dto.PaymentInitiationResponse;
import com.cateringmarketplace.module.payment.model.Transaction;

import java.util.Map;

/**
 * Payment gateway strategy interface.
 * Implementations will handle specific payment gateways (Stripe, Razorpay, etc.)
 */
public interface PaymentGatewayStrategy {

    /**
     * Gets the payment gateway name.
     */
    String getGatewayName();

    /**
     * Creates a payment intent/order in the gateway.
     */
    PaymentInitiationResponse createPaymentIntent(PaymentInitiationRequest request);

    /**
     * Verifies a payment.
     */
    boolean verifyPayment(String gatewayOrderId, String gatewayPaymentId, String signature);

    /**
     * Processes a webhook from the gateway.
     */
    Map<String, Object> processWebhook(String payload, String signature);

    /**
     * Creates a refund.
     */
    boolean createRefund(Transaction transaction, String reason);

    /**
     * Checks if this gateway supports the given country.
     */
    boolean supportsCountry(String country);
}

