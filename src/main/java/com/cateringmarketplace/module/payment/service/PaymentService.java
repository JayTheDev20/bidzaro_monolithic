package com.cateringmarketplace.module.payment.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.payment.model.Transaction;
import com.cateringmarketplace.module.payment.model.Transaction.*;
import com.cateringmarketplace.module.payment.repository.TransactionRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service class for payment operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key-id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret:}")
    private String razorpayKeySecret;

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

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .orderId(orderId)
                .userId(userId)
                .paymentType(paymentType)
                .amount(TransactionAmount.builder()
                        .currency("INR")
                        .amount(amount)
                        .build())
                .paymentGateway(PaymentGateway.RAZORPAY)
                .status(TransactionStatus.PENDING)
                .initiatedAt(Instant.now())
                .metadata(TransactionMetadata.builder().build())
                .build();

        // Create Razorpay order
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", transaction.getTransactionId());
            orderRequest.put("notes", new JSONObject()
                    .put("orderId", orderId)
                    .put("userId", userId)
                    .put("paymentType", paymentType.name()));

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

            transaction.setGatewayOrderId(razorpayOrder.get("id"));
            transaction = transactionRepository.save(transaction);

            log.info("Payment initiated. Transaction: {}, Razorpay Order: {}",
                    transaction.getTransactionId(), razorpayOrder.get("id"));

            return PaymentInitiationResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .gatewayOrderId(razorpayOrder.get("id"))
                    .amount(amount)
                    .currency("INR")
                    .keyId(razorpayKeyId)
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
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

        // Verify signature
        try {
            String payload = gatewayOrderId + "|" + gatewayPaymentId;
            boolean isValid = verifyRazorpaySignature(payload, signature);

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
            return transaction;

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            throw new BadRequestException("VERIFICATION_FAILED", "Payment verification failed");
        }
    }

    /**
     * Handles Razorpay webhook.
     */
    @Transactional
    public void handleWebhook(String payload, String signature) {
        log.info("Processing Razorpay webhook");

        // Verify webhook signature
        // Parse payload and update transaction status
        // This is a simplified implementation

        try {
            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            if ("payment.captured".equals(eventType)) {
                JSONObject paymentEntity = event.getJSONObject("payload")
                        .getJSONObject("payment").getJSONObject("entity");

                String orderId = paymentEntity.getString("order_id");
                String paymentId = paymentEntity.getString("id");

                transactionRepository.findByGatewayOrderId(orderId)
                        .ifPresent(transaction -> {
                            transaction.setGatewayTransactionId(paymentId);
                            transaction.setStatus(TransactionStatus.SUCCESS);
                            transaction.setProcessedAt(Instant.now());
                            transactionRepository.save(transaction);
                            updateOrderPaymentStatus(transaction);
                        });
            }

        } catch (Exception e) {
            log.error("Webhook processing failed: {}", e.getMessage());
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

    private boolean verifyRazorpaySignature(String payload, String signature) {
        // In production, use Razorpay SDK to verify signature
        // This is a placeholder
        return signature != null && !signature.isEmpty();
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

    // Response DTOs
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentInitiationResponse {
        private String transactionId;
        private String gatewayOrderId;
        private BigDecimal amount;
        private String currency;
        private String keyId;
    }
}

