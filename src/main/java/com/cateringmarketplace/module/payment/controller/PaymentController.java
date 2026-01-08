package com.cateringmarketplace.module.payment.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.payment.model.Transaction;
import com.cateringmarketplace.module.payment.model.Transaction.PaymentType;
import com.cateringmarketplace.module.payment.service.PaymentService;
import com.cateringmarketplace.module.payment.service.PaymentService.PaymentInitiationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for payment operations.
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Initiates a payment for an order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PaymentInitiationResponse>> initiatePayment(
            @RequestParam String orderId,
            @RequestParam String paymentType,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Initiating payment for order: {} by user: {}", orderId, userDetails.getUserId());
        PaymentType type = PaymentType.valueOf(paymentType.toUpperCase());
        PaymentInitiationResponse response = paymentService.initiatePayment(
                orderId, type, amount, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response, "Payment initiated"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verifies payment after completion")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Transaction>> verifyPayment(
            @RequestParam String gatewayOrderId,
            @RequestParam String gatewayPaymentId,
            @RequestParam String signature) {

        log.info("Verifying payment. Order: {}, Payment: {}", gatewayOrderId, gatewayPaymentId);
        Transaction transaction = paymentService.verifyPayment(gatewayOrderId, gatewayPaymentId, signature);

        return ResponseEntity.ok(ApiResponse.success(transaction, "Payment verified successfully"));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get user transactions", description = "Returns user's transaction history")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<Transaction>>> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions = paymentService.getUserTransactions(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                transactions.getContent(),
                "Transactions retrieved",
                PageInfo.from(transactions)
        ));
    }

    @GetMapping("/transactions/{transactionId}")
    @Operation(summary = "Get transaction", description = "Returns transaction details by ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(
            @PathVariable String transactionId) {
        Transaction transaction = paymentService.getTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order transactions", description = "Returns transactions for an order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<Transaction>>> getOrderTransactions(
            @PathVariable String orderId) {
        List<Transaction> transactions = paymentService.getOrderTransactions(orderId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    // ==================== WEBHOOK ENDPOINTS ====================

    @PostMapping("/webhook/razorpay")
    @Operation(summary = "Razorpay webhook", description = "Handles Razorpay payment webhooks")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook");
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("OK");
    }
}

