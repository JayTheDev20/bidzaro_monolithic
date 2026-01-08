package com.cateringmarketplace.module.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for vendor dashboard analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDashboardResponse {

    private String vendorId;
    private String vendorName;
    private VendorMetrics metrics;
    private BidMetrics bidMetrics;
    private List<RecentOrder> recentOrders;
    private List<UpcomingEvent> upcomingEvents;
    private PerformanceMetrics performance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorMetrics {
        private long totalOrders;
        private long completedOrders;
        private long pendingOrders;
        private long cancelledOrders;
        private BigDecimal totalRevenue;
        private BigDecimal pendingPayouts;
        private BigDecimal thisMonthRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidMetrics {
        private long totalBidsSubmitted;
        private long acceptedBids;
        private long pendingBids;
        private double acceptanceRate;
        private double averageBidAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrder {
        private String orderId;
        private String eventName;
        private String eventDate;
        private int guestCount;
        private BigDecimal amount;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingEvent {
        private String orderId;
        private String eventName;
        private String eventDate;
        private String eventTime;
        private String venue;
        private int guestCount;
        private int daysUntil;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private double averageRating;
        private int totalReviews;
        private double responseRate;
        private double onTimeDeliveryRate;
        private int repeatCustomers;
    }
}

