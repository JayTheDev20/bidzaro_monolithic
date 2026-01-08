package com.cateringmarketplace.module.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaction entity representing payment transactions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("transaction_id")
    private String transactionId;

    @Indexed
    @Field("order_id")
    private String orderId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("vendor_id")
    private String vendorId;

    @Field("payment_type")
    private PaymentType paymentType;

    @Field("installment_number")
    private Integer installmentNumber;

    private TransactionAmount amount;

    @Field("payment_gateway")
    private PaymentGateway paymentGateway;

    @Field("gateway_transaction_id")
    private String gatewayTransactionId;

    @Field("gateway_order_id")
    private String gatewayOrderId;

    @Field("payment_method")
    private PaymentMethod paymentMethod;

    @Field("payment_method_details")
    private PaymentMethodDetails paymentMethodDetails;

    @Indexed
    private TransactionStatus status;

    @Field("failure_reason")
    private String failureReason;

    @Field("initiated_at")
    private Instant initiatedAt;

    @Field("processed_at")
    private Instant processedAt;

    @Field("settled_at")
    private Instant settledAt;

    private TransactionMetadata metadata;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // Enums
    public enum PaymentType {
        TOKEN,
        INSTALLMENT,
        FINAL,
        REFUND
    }

    public enum PaymentGateway {
        RAZORPAY,
        STRIPE,
        PAYPAL
    }

    public enum PaymentMethod {
        CARD,
        UPI,
        NET_BANKING,
        WALLET
    }

    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionAmount {
        @Builder.Default
        private String currency = "INR";
        private BigDecimal amount;
        private BigDecimal platformFee;
        private BigDecimal vendorPayout;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodDetails {
        private String cardLastFour;
        private String cardBrand;
        private String cardNetwork;
        private String upiId;
        private String bankName;
        private String walletName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionMetadata {
        private String ipAddress;
        private String userAgent;
        private String description;
    }

    /**
     * Checks if transaction is successful.
     */
    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCESS;
    }

    /**
     * Checks if transaction is pending.
     */
    public boolean isPending() {
        return status == TransactionStatus.PENDING || status == TransactionStatus.PROCESSING;
    }

    /**
     * Marks transaction as successful.
     */
    public void markSuccess(String gatewayTransactionId) {
        this.status = TransactionStatus.SUCCESS;
        this.gatewayTransactionId = gatewayTransactionId;
        this.processedAt = Instant.now();
    }

    /**
     * Marks transaction as failed.
     */
    public void markFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.processedAt = Instant.now();
    }
}

