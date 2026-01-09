package com.cateringmarketplace.module.payment.gateway.impl;

import com.cateringmarketplace.module.payment.dto.PaymentInitiationRequest;
import com.cateringmarketplace.module.payment.dto.PaymentInitiationResponse;
import com.cateringmarketplace.module.payment.gateway.PaymentGatewayStrategy;
import com.cateringmarketplace.module.payment.model.Transaction;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Stripe payment gateway implementation for USA.
 */
@Component
@Slf4j
public class StripePaymentGateway implements PaymentGatewayStrategy {

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @Value("${stripe.publishable-key:}")
    private String publishableKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isEmpty() && !secretKey.startsWith("your_")) {
            Stripe.apiKey = secretKey;
            log.info("Stripe payment gateway initialized for USA");
        } else {
            log.warn("Stripe API key not configured - payment processing will be disabled");
        }
    }

    @Override
    public String getGatewayName() {
        return "STRIPE";
    }

    @Override
    public PaymentInitiationResponse createPaymentIntent(PaymentInitiationRequest request) {
        try {
            // Stripe amounts are in cents
            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription("Order: " + request.getOrderId())
                    .putMetadata("orderId", request.getOrderId())
                    .putMetadata("userId", request.getUserId())
                    .putMetadata("transactionId", request.getTransactionId())
                    .putMetadata("paymentType", request.getPaymentType())
                    .setReceiptEmail(request.getCustomerEmail())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("Stripe PaymentIntent created: {}", paymentIntent.getId());

            return PaymentInitiationResponse.builder()
                    .transactionId(request.getTransactionId())
                    .gatewayOrderId(paymentIntent.getId())
                    .gatewayName("STRIPE")
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .clientSecret(paymentIntent.getClientSecret())
                    .publishableKey(publishableKey)
                    .build();

        } catch (StripeException e) {
            log.error("Failed to create Stripe PaymentIntent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate Stripe payment: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(String paymentIntentId, String paymentMethodId, String signature) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Check if payment is successful
            return "succeeded".equals(paymentIntent.getStatus());

        } catch (StripeException e) {
            log.error("Failed to verify Stripe payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> processWebhook(String payload, String signature) {
        Map<String, Object> result = new HashMap<>();

        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);

            log.info("Processing Stripe webhook event: {}", event.getType());

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElse(null);

                    if (paymentIntent != null) {
                        result.put("eventType", "payment.captured");
                        result.put("gatewayOrderId", paymentIntent.getId());
                        result.put("gatewayTransactionId", paymentIntent.getId());
                        result.put("status", "SUCCESS");
                        result.put("metadata", paymentIntent.getMetadata());
                    }
                    break;

                case "payment_intent.payment_failed":
                    PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElse(null);

                    if (failedIntent != null) {
                        result.put("eventType", "payment.failed");
                        result.put("gatewayOrderId", failedIntent.getId());
                        result.put("status", "FAILED");
                        result.put("failureReason", failedIntent.getLastPaymentError() != null ?
                                failedIntent.getLastPaymentError().getMessage() : "Payment failed");
                    }
                    break;

                case "charge.refunded":
                    result.put("eventType", "refund.processed");
                    result.put("status", "REFUNDED");
                    break;

                default:
                    log.info("Unhandled Stripe webhook event type: {}", event.getType());
                    result.put("eventType", "unknown");
            }

            result.put("success", true);

        } catch (Exception e) {
            log.error("Stripe webhook processing failed: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean createRefund(Transaction transaction, String reason) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(transaction.getGatewayOrderId())
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .putMetadata("orderId", transaction.getOrderId())
                    .putMetadata("reason", reason)
                    .build();

            Refund refund = Refund.create(params);

            log.info("Stripe refund created: {}", refund.getId());
            return "succeeded".equals(refund.getStatus());

        } catch (StripeException e) {
            log.error("Failed to create Stripe refund: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean supportsCountry(String country) {
        // Stripe supports USA and many other countries
        return "USA".equalsIgnoreCase(country) ||
               "US".equalsIgnoreCase(country) ||
               "UNITED STATES".equalsIgnoreCase(country);
    }
}

