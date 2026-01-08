package com.cateringmarketplace.module.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for platform analytics overview.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewResponse {

    private PlatformMetrics platformMetrics;
    private GrowthMetrics growthMetrics;
    private List<TopVendor> topVendors;
    private List<TopCategory> topCategories;
    private Map<String, Long> ordersByStatus;
    private List<RevenueDataPoint> revenueChart;

    // =========================================================
    // PLATFORM METRICS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformMetrics {

        private long totalUsers;
        private long totalVendors;
        private long totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal platformEarnings;
        private double averageOrderValue;
        private double conversionRate;
    }

    // =========================================================
    // GROWTH METRICS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthMetrics {

        private double userGrowthPercentage;
        private double vendorGrowthPercentage;
        private double orderGrowthPercentage;
        private double revenueGrowthPercentage;
    }

    // =========================================================
    // TOP VENDORS
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopVendor {

        private String vendorId;
        private String vendorName;
        private long totalOrders;
        private BigDecimal totalRevenue;
        private double rating;
    }

    // =========================================================
    // TOP CATEGORIES
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCategory {

        private String categoryId;
        private String categoryName;
        private long orderCount;
        private BigDecimal revenue;
    }

    // =========================================================
    // REVENUE CHART
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDataPoint {

        private String date;
        private BigDecimal revenue;
        private long orderCount;
    }
}
