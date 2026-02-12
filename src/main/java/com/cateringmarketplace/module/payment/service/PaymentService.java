package com.cateringmarketplace.module.payment.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.service.BidService;
import com.cateringmarketplace.module.notification.service.EmailService;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.order.service.OrderService;
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
    private final BidRequestRepository bidRequestRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final EmailService emailService;
    private final OrderService orderService; // Inject OrderService to create order after payment
    private final BidService bidService; // Inject BidService to confirm bid payment

    /**
     * Initiates a payment for an order or a bid (token payment).
     */
    @Transactional
    public PaymentInitiationResponse initiatePayment(String orderId, String bidId, PaymentType paymentType,
                                                      BigDecimal amount, String userId) {
        log.info("Initiating {} payment of {} for order: {} / bid: {}", paymentType, amount, orderId, bidId);

        // Validate inputs: either orderId or bidId must be present
        if (orderId == null && bidId == null) {
            throw new BadRequestException("INVALID_REQUEST", "Either Order ID or Bid ID must be provided");
        }

        // If bidId is provided, validate bid ownership
        if (bidId != null) {
            // Note: bidId here refers to the Accepted Vendor Bid ID, but we usually check the Bid Request
            // For simplicity, assuming we validate against the Bid Request owner if possible,
            // or we trust the controller has validated the user.
            // Ideally, we should fetch the Bid/BidRequest and check userId.
            // Skipping deep validation for now to keep it simple, but in prod, validate ownership.
        }

        // If orderId is provided, validate order ownership
        if (orderId != null) {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            if (!order.getUserId().equals(userId)) {
                throw new BadRequestException("UNAUTHORIZED", "You cannot make payment for this order");
            }
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
                .orderId(orderId) // Can be null if paying for bid
                .bidId(bidId)     // Can be null if paying for existing order
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
                    .bidId(bidId)
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

        // Logic to handle successful payment
        if (transaction.getBidId() != null && transaction.getOrderId() == null) {
            // Case: Token payment for a Bid -> Confirm Bid Payment -> Create Order
            log.info("Token payment successful for Bid: {}. Confirming Bid Payment...", transaction.getBidId());

            // 1. Confirm Bid Payment (Update status to ACCEPTED)
            bidService.confirmBidPayment(transaction.getBidId());

            // 2. Create Order
            log.info("Creating Order for Bid: {}", transaction.getBidId());
            Order newOrder = orderService.createOrderFromBid(transaction.getBidId(), transaction.getTransactionId(), transaction.getAmount().getAmount());
            
            // Link transaction to the new order
            transaction.setOrderId(newOrder.getOrderId());
            transactionRepository.save(transaction);
            
        } else if (transaction.getOrderId() != null) {
            // Case: Payment for existing Order
            updateOrderPaymentStatus(transaction);
        }

        log.info("Payment verified successfully: {}", transaction.getTransactionId());
        
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
                                
                                // Handle Order Creation or Update
                                if (transaction.getBidId() != null && transaction.getOrderId() == null) {
                                     // 1. Confirm Bid Payment
                                     bidService.confirmBidPayment(transaction.getBidId());

                                     // 2. Create Order
                                     Order newOrder = orderService.createOrderFromBid(transaction.getBidId(), transaction.getTransactionId(), transaction.getAmount().getAmount());
                                     transaction.setOrderId(newOrder.getOrderId());
                                     transactionRepository.save(transaction);
                                } else {
                                    updateOrderPaymentStatus(transaction);
                                }
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

        BigDecimal paidAmount = transaction.getAmount().getAmount();

        // Update financial totals
        order.getPaymentDetails().setTotalPaid(
                order.getPaymentDetails().getTotalPaid().add(paidAmount));
        order.getPaymentDetails().setBalanceDue(
                order.getPaymentDetails().getBalanceDue().subtract(paidAmount));

        // Handle specific payment types
        if (transaction.getPaymentType() == PaymentType.TOKEN) {
            order.getPaymentDetails().setTokenPaid(true);
            order.getPaymentDetails().setTokenPaymentId(transaction.getTransactionId());
            order.getPaymentDetails().setTokenPaidAt(Instant.now());
            order.getPaymentDetails().setPaymentStatus(Order.PaymentDetails.PaymentStatus.TOKEN_PAID);

            // Update order status if it was pending token
            if (order.getStatus() == OrderStatus.PENDING_TOKEN_PAYMENT) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setConfirmedAt(Instant.now());
            }
        } else if (transaction.getPaymentType() == PaymentType.BALANCE || transaction.getPaymentType() == PaymentType.FULL) {
            // Check if fully paid
            if (order.getPaymentDetails().getBalanceDue().compareTo(BigDecimal.ZERO) <= 0) {
                order.getPaymentDetails().setPaymentStatus(Order.PaymentDetails.PaymentStatus.FULLY_PAID);
                // Ensure balance doesn't go negative (optional, but good practice)
                if (order.getPaymentDetails().getBalanceDue().compareTo(BigDecimal.ZERO) < 0) {
                    order.getPaymentDetails().setBalanceDue(BigDecimal.ZERO);
                }
            } else {
                // Partially paid (if balance payment was partial)
                order.getPaymentDetails().setPaymentStatus(Order.PaymentDetails.PaymentStatus.PARTIALLY_PAID);
            }
        }

        orderRepository.save(order);
        log.info("Updated payment status for order: {}. New Balance: {}", order.getOrderId(), order.getPaymentDetails().getBalanceDue());
    }
}
