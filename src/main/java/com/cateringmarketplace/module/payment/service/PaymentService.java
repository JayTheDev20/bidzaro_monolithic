package com.cateringmarketplace.module.payment.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.payment.dto.PaymentInitiationRequest;
import com.cateringmarketplace.module.payment.dto.PaymentInitiationResponse;
import com.cateringmarketplace.module.payment.gateway.PaymentGatewayFactory;
import com.cateringmarketplace.module.payment.gateway.PaymentGatewayStrategy;
import com.cateringmarketplace.module.payment.model.Transaction;
import com.cateringmarketplace.module.payment.model.Transaction.*;
import com.cateringmarketplace.module.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for payment operations.
 * Supports multiple payment gateways: Stripe (USA) and Razorpay (India).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final EmailService emailService;

    /**
     * Initiates a payment for an order.
     */
    @Transactional
    public PaymentInitiationResponse initiatePayment(String orderId, PaymentType paymentType,
                                                      BigDecimal amount, String userId) {
        log.info("Initiating {} payment of {} for order: {}", paymentType, amount, orderId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("UNAUTHORIZED", "You cannot make payment for this order");
        }

        // Get user to determine country and currency
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String country = user.getCountry() != null ? user.getCountry() : "USA";
        String currency = determineCurrency(country);

        // Select appropriate payment gateway based on country
        PaymentGatewayStrategy gateway = gatewayFactory.getGatewayForCountry(country);
        PaymentGateway gatewayEnum = PaymentGateway.valueOf(gateway.getGatewayName());

        // Create transaction record
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .orderId(orderId)
                .userId(userId)
                .paymentType(paymentType)
                .amount(TransactionAmount.builder()
                        .currency(currency)
                        .amount(amount)
                        .build())
                .paymentGateway(gatewayEnum)
                .status(TransactionStatus.PENDING)
                .initiatedAt(Instant.now())
                .metadata(TransactionMetadata.builder().build())
                .build();

        transaction = transactionRepository.save(transaction);

        // Create payment intent using selected gateway
        try {
            PaymentInitiationRequest request = PaymentInitiationRequest.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .transactionId(transactionId)
                    .amount(amount)
                    .currency(currency)
                    .paymentType(paymentType.name())
                    .customerEmail(user.getEmail())
                    .customerName(user.getFirstName() + " " + user.getLastName())
                    .country(country)
                    .build();

            PaymentInitiationResponse response = gateway.createPaymentIntent(request);

            // Update transaction with gateway order ID
            transaction.setGatewayOrderId(response.getGatewayOrderId());
            transactionRepository.save(transaction);

            log.info("Payment initiated via {}. Transaction: {}, Gateway Order: {}",
                    gateway.getGatewayName(), transactionId, response.getGatewayOrderId());

            return response;

        } catch (Exception e) {
            log.error("Failed to initiate payment via {}: {}", gateway.getGatewayName(), e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            throw new BadRequestException("PAYMENT_FAILED", "Failed to initiate payment: " + e.getMessage());
        }
    }

    /**
     * Verifies payment after completion.
     */
    @Transactional
    public Transaction verifyPayment(String gatewayOrderId, String gatewayPaymentId, String signature) {
        log.info("Verifying payment. Order: {}, Payment: {}", gatewayOrderId, gatewayPaymentId);

        Transaction transaction = transactionRepository.findByGatewayOrderId(gatewayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Get the gateway used for this transaction
        PaymentGatewayStrategy gateway = gatewayFactory.getGatewayByName(transaction.getPaymentGateway().name());

        // Verify payment using the appropriate gateway
        boolean isValid = gateway.verifyPayment(gatewayOrderId, gatewayPaymentId, signature);

        if (!isValid) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason("Invalid payment signature");
            transactionRepository.save(transaction);
            throw new BadRequestException("INVALID_SIGNATURE", "Payment verification failed");
        }

        // Update transaction
        transaction.setGatewayTransactionId(gatewayPaymentId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setProcessedAt(Instant.now());
        transaction = transactionRepository.save(transaction);

        // Update order payment status
        updateOrderPaymentStatus(transaction);

        log.info("Payment verified successfully: {}", transaction.getTransactionId());
        
        // Send payment confirmation email
        // Assuming we have user email, skipping for now as we don't have user entity here
        // In real scenario: emailService.sendPaymentConfirmationEmail(userEmail, transaction.getTransactionId(), transaction.getAmount().getAmount().doubleValue(), transaction.getPaymentType().name(), "USER");

        return transaction;
    }

    /**
     * Handles payment gateway webhook.
     */
    @Transactional
    public void handleWebhook(String gatewayName, String payload, String signature) {
        log.info("Processing {} webhook", gatewayName);

        try {
            PaymentGatewayStrategy gateway = gatewayFactory.getGatewayByName(gatewayName);
            Map<String, Object> result = gateway.processWebhook(payload, signature);

            if (Boolean.TRUE.equals(result.get("success"))) {
                String eventType = (String) result.get("eventType");

                if ("payment.captured".equals(eventType) || "payment_intent.succeeded".equals(eventType)) {
                    String gatewayOrderId = (String) result.get("gatewayOrderId");
                    String gatewayTransactionId = (String) result.get("gatewayTransactionId");

                    transactionRepository.findByGatewayOrderId(gatewayOrderId)
                            .ifPresent(transaction -> {
                                transaction.setGatewayTransactionId(gatewayTransactionId);
                                transaction.setStatus(TransactionStatus.SUCCESS);
                                transaction.setProcessedAt(Instant.now());
                                transactionRepository.save(transaction);
                                updateOrderPaymentStatus(transaction);
                            });
                } else if ("payment.failed".equals(eventType) || "payment_intent.payment_failed".equals(eventType)) {
                    String gatewayOrderId = (String) result.get("gatewayOrderId");
                    String failureReason = (String) result.get("failureReason");

                    transactionRepository.findByGatewayOrderId(gatewayOrderId)
                            .ifPresent(transaction -> {
                                transaction.setStatus(TransactionStatus.FAILED);
                                transaction.setFailureReason(failureReason);
                                transactionRepository.save(transaction);
                            });
                }
            }

        } catch (Exception e) {
            log.error("{} webhook processing failed: {}", gatewayName, e.getMessage(), e);
        }
    }

    /**
     * Gets transactions for an order.
     */
    public List<Transaction> getOrderTransactions(String orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    /**
     * Gets transactions for a user.
     */
    public Page<Transaction> getUserTransactions(String userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }

    /**
     * Gets transaction by ID.
     */
    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    // ==================== HELPER METHODS ====================

    /**
     * Determines currency based on country.
     */
    private String determineCurrency(String country) {
        if (country == null || country.isEmpty()) {
            return "USD"; // Default
        }

        // USA uses USD
        if ("USA".equalsIgnoreCase(country) || "US".equalsIgnoreCase(country) ||
            "UNITED STATES".equalsIgnoreCase(country)) {
            return "USD";
        }

        // India uses INR
        if ("INDIA".equalsIgnoreCase(country) || "IN".equalsIgnoreCase(country)) {
            return "INR";
        }

        // Default to USD for other countries
        return "USD";
    }

    private void updateOrderPaymentStatus(Transaction transaction) {
        Order order = orderRepository.findByOrderId(transaction.getOrderId())
                .orElse(null);

        if (order == null) return;

        if (transaction.getPaymentType() == PaymentType.TOKEN) {
            order.getPaymentDetails().setTokenPaid(true);
            order.getPaymentDetails().setTokenPaymentId(transaction.getTransactionId());
            order.getPaymentDetails().setTokenPaidAt(Instant.now());
            order.getPaymentDetails().setTotalPaid(
                    order.getPaymentDetails().getTotalPaid().add(transaction.getAmount().getAmount()));
            order.getPaymentDetails().setBalanceDue(
                    order.getPaymentDetails().getBalanceDue().subtract(transaction.getAmount().getAmount()));
            order.getPaymentDetails().setPaymentStatus(Order.PaymentDetails.PaymentStatus.TOKEN_PAID);

            // Update order status
            if (order.getStatus() == OrderStatus.PENDING_TOKEN_PAYMENT) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setConfirmedAt(Instant.now());
            }
        }

        orderRepository.save(order);
    }
}

