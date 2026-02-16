package com.cateringmarketplace.module.order.repository;

import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity operations.
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    /**
     * Find by order ID.
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find orders by user ID.
     */
    Page<Order> findByUserId(String userId, Pageable pageable);

    /**
     * Find orders by user ID and status.
     */
    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);

    /**
     * Find orders by vendor ID.
     */
    @Query("{'vendor_orders.vendorId': ?0}")
    Page<Order> findByVendorId(String vendorId, Pageable pageable);

    /**
     * Find orders by status.
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Find orders by bid request ID.
     */
    Optional<Order> findByBidRequestId(String bidRequestId);

    /**
     * Find upcoming orders (event date in future).
     */
    @Query("{'event_details.event_date': {'$gte': ?0}, 'status': {'$nin': ['CANCELLED', 'COMPLETED']}}")
    Page<Order> findUpcomingOrders(LocalDate today, Pageable pageable);

    /**
     * Find orders by event date.
     */
    @Query("{'event_details.event_date': ?0}")
    List<Order> findByEventDate(LocalDate date);

    /**
     * Find orders pending token payment.
     */
    @Query("{'status': 'PENDING_TOKEN_PAYMENT', 'created_at': {'$lt': ?0}}")
    List<Order> findPendingPaymentOrders(Instant cutoffTime);

    /**
     * Find delivered orders for completion.
     */
    @Query("{'status': 'DELIVERED', 'delivered_at': {'$lt': ?0}}")
    List<Order> findOrdersForCompletion(Instant cutoffTime);

    /**
     * Count orders by status.
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders by user ID.
     */
    long countByUserId(String userId);

    /**
     * Count orders by vendor ID.
     */
    @Query(value = "{'vendor_orders.vendorId': ?0}", count = true)
    long countByVendorId(String vendorId);

    /**
     * Find orders created between dates.
     */
    List<Order> findByCreatedAtBetween(Instant start, Instant end);

    /**
     * Find orders with events in date range.
     */
    @Query("{'event_details.event_date': {'$gte': ?0, '$lte': ?1}}")
    List<Order> findByEventDateBetween(LocalDate start, LocalDate end);

    /**
     * Count orders created after a date.
     */
    long countByCreatedAtAfter(Instant date);
}
