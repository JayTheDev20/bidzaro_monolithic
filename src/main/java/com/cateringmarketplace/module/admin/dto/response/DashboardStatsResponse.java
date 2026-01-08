package com.cateringmarketplace.module.admin.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for admin dashboard statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private UserStats userStats;
    private VendorStats vendorStats;
    private OrderStats orderStats;
    private RevenueStats revenueStats;
    private BidStats bidStats;

    // =========================================================
    // USER STATS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {

        private long totalUsers;
        private long activeUsers;
        private long newUsersToday;
        private long newUsersThisWeek;
        private long newUsersThisMonth;
    }

    // =========================================================
    // VENDOR STATS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorStats {

        private long totalVendors;
        private long activeVendors;
        private long pendingApproval;
        private long verifiedVendors;
        private long newVendorsThisMonth;
    }

    // =========================================================
    // ORDER STATS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStats {

        private long totalOrders;
        private long pendingOrders;
        private long completedOrders;
        private long cancelledOrders;
        private long ordersToday;
        private long ordersThisWeek;
        private long ordersThisMonth;
    }

    // =========================================================
    // REVENUE STATS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStats {

        private BigDecimal totalRevenue;
        private BigDecimal revenueToday;
        private BigDecimal revenueThisWeek;
        private BigDecimal revenueThisMonth;
        private BigDecimal platformFees;
        private BigDecimal pendingPayouts;
    }

    // =========================================================
    // BID STATS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidStats {

        private long totalBidRequests;
        private long activeBidRequests;
        private long totalBidsSubmitted;
        private long acceptedBids;
        private double averageBidsPerRequest;
    }
}
