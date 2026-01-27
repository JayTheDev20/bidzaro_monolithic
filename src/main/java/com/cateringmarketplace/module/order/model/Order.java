package com.cateringmarketplace.module.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing confirmed catering orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("order_id")
    private String orderId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("bid_request_id")
    private String bidRequestId;

    @Field("event_details")
    private EventDetails eventDetails;

    @Field("vendor_orders")
    @Builder.Default
    private List<VendorOrder> vendorOrders = new ArrayList<>();

    private OrderPricing pricing;

    @Field("payment_details")
    private PaymentDetails paymentDetails;

    @Field("contact_info")
    private ContactInfo contactInfo;

    @Field("special_instructions")
    private String specialInstructions;

    @Indexed
    private OrderStatus status;

    private Cancellation cancellation;

    private OrderRatings ratings;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("confirmed_at")
    private Instant confirmedAt;

    @Field("delivered_at")
    private Instant deliveredAt;

    @Field("completed_at")
    private Instant completedAt;

    // Enums
    public enum OrderStatus {
        PENDING_TOKEN_PAYMENT,
        CONFIRMED,
        IN_PREPARATION,
        READY_FOR_DELIVERY,
        DELIVERING,
        DELIVERED,
        COMPLETED,
        CANCELLED
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetails {
        private String eventType;
        private String eventName;
        @Indexed
        private LocalDate eventDate;
        private String eventTime;
        private Integer numberOfGuests;
        private VenueAddress venueAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueAddress {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private GeoJsonPoint gpsCoordinates;
        private String landmark;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorOrder {
        private String vendorOrderId;
        @Indexed
        private String vendorId;
        private String vendorName;
        @Builder.Default
        private List<OrderItem> items = new ArrayList<>();
        private BigDecimal subtotal;
        private BigDecimal serviceCharge;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private VendorOrderStatus vendorStatus;
        private DeliveryStatus deliveryStatus;
        private Instant estimatedDeliveryTime;
        private Instant actualDeliveryTime;

        public enum VendorOrderStatus {
            PENDING,
            ACCEPTED, // Added ACCEPTED status
            CONFIRMED,
            IN_PREPARATION,
            READY,
            DELIVERED,
            COMPLETED
        }

        public enum DeliveryStatus {
            PENDING,
            ON_THE_WAY,
            DELIVERED
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String vendorItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal pricePerPlate;
        private BigDecimal totalPrice;
        @Builder.Default
        private List<ItemCustomization> customizations = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCustomization {
        private String optionName;
        private String selectedChoice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderPricing {
        @Builder.Default
        private String currency = "INR";
        private BigDecimal subtotal;
        private BigDecimal serviceCharges;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal platformFee;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetails {
        private BigDecimal tokenAmount;
        @Builder.Default
        private Boolean tokenPaid = false;
        private String tokenPaymentId;
        private Instant tokenPaidAt;
        @Builder.Default
        private List<PaymentInstallment> paymentSchedule = new ArrayList<>();
        @Builder.Default
        private BigDecimal totalPaid = BigDecimal.ZERO;
        private BigDecimal balanceDue;
        private PaymentStatus paymentStatus;

        public enum PaymentStatus {
            TOKEN_PENDING,
            TOKEN_PAID,
            PARTIALLY_PAID,
            FULLY_PAID
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInstallment {
        private Integer installmentNumber;
        private Instant dueDate;
        private BigDecimal amount;
        private InstallmentStatus status;
        private String paymentId;
        private Instant paidAt;

        public enum InstallmentStatus {
            PENDING,
            PAID
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String primaryContactName;
        private String primaryContactPhone;
        private String primaryContactEmail;
        private String alternateContactPhone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancellation {
        @Builder.Default
        private Boolean isCancelled = false;
        private String cancelledBy;
        private String cancelledByType;
        private String cancellationReason;
        private Instant cancelledAt;
        private BigDecimal refundAmount;
        private String refundStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderRatings {
        @Builder.Default
        private Boolean userRated = false;
        @Builder.Default
        private Boolean vendorRatedByUser = false;
    }

    /**
     * Checks if order is cancellable.
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING_TOKEN_PAYMENT ||
               status == OrderStatus.CONFIRMED;
    }

    /**
     * Checks if order is completed.
     */
    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }

    /**
     * Gets days until event.
     */
    public long getDaysUntilEvent() {
        if (eventDetails == null || eventDetails.getEventDate() == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), eventDetails.getEventDate());
    }

    /**
     * Calculates refund percentage based on cancellation policy.
     */
    public BigDecimal calculateRefundPercentage() {
        long daysUntilEvent = getDaysUntilEvent();

        if (daysUntilEvent >= 30) return new BigDecimal("100");
        if (daysUntilEvent >= 15) return new BigDecimal("75");
        if (daysUntilEvent >= 7) return new BigDecimal("50");
        if (daysUntilEvent >= 3) return new BigDecimal("25");
        return BigDecimal.ZERO;
    }
}
