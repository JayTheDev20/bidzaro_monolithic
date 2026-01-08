package com.cateringmarketplace.module.payment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.payment.model.Transaction;
import com.cateringmarketplace.module.payment.model.Transaction.PaymentType;
import com.cateringmarketplace.module.payment.model.Transaction.TransactionStatus;

/**
 * Repository for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // =========================================================
    // FIND BY IDS
    // =========================================================

    /**
     * Find by transaction ID.
     */
    Optional<Transaction> findByTransactionId(String transactionId);

    /**
     * Find by gateway transaction ID.
     */
    Optional<Transaction> findByGatewayTransactionId(String gatewayTransactionId);

    /**
     * Find by gateway order ID.
     */
    Optional<Transaction> findByGatewayOrderId(String gatewayOrderId);

    // =========================================================
    // ORDER BASED QUERIES
    // =========================================================

    /**
     * Find transactions by order ID.
     */
    List<Transaction> findByOrderId(String orderId);

    /**
     * Find transactions by order ID with pagination.
     */
    Page<Transaction> findByOrderId(String orderId, Pageable pageable);

    /**
     * Find successful transactions for an order.
     */
    @Query("{'order_id': ?0, 'status': 'SUCCESS'}")
    List<Transaction> findSuccessfulTransactionsForOrder(String orderId);

    /**
     * Count successful transactions for an order.
     */
    @Query(value = "{'order_id': ?0, 'status': 'SUCCESS'}", count = true)
    long countSuccessfulTransactionsForOrder(String orderId);

    // =========================================================
    // USER / VENDOR
    // =========================================================

    /**
     * Find transactions by user ID.
     */
    Page<Transaction> findByUserId(String userId, Pageable pageable);

    /**
     * Find transactions by vendor ID.
     */
    Page<Transaction> findByVendorId(String vendorId, Pageable pageable);

    // =========================================================
    // STATUS & TYPE
    // =========================================================

    /**
     * Find transactions by status.
     */
    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);

    /**
     * Count transactions by status.
     */
    long countByStatus(TransactionStatus status);

    /**
     * Find transactions by payment type.
     */
    Page<Transaction> findByPaymentType(PaymentType type, Pageable pageable);

    // =========================================================
    // DATE / TIME
    // =========================================================

    /**
     * Find transactions by date range.
     */
    List<Transaction> findByCreatedAtBetween(Instant start, Instant end);

    /**
     * Find pending transactions older than a time.
     */
    @Query("{'status': 'PENDING', 'initiated_at': {'$lt': ?0}}")
    List<Transaction> findStalePendingTransactions(Instant cutoff);
}
