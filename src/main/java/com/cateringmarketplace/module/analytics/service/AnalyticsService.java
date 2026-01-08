package com.cateringmarketplace.module.analytics.service;

import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.analytics.dto.response.AnalyticsOverviewResponse;
import com.cateringmarketplace.module.analytics.dto.response.AnalyticsOverviewResponse.*;
import com.cateringmarketplace.module.analytics.dto.response.VendorDashboardResponse;
import com.cateringmarketplace.module.analytics.dto.response.VendorDashboardResponse.*;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.payment.repository.TransactionRepository;
import com.cateringmarketplace.module.review.repository.ReviewRepository;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for analytics operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final BidRequestRepository bidRequestRepository;
    private final VendorBidRepository vendorBidRepository;
    private final TransactionRepository transactionRepository;
    private final ReviewRepository reviewRepository;

    // ==================== PLATFORM ANALYTICS (ADMIN) ====================

    public AnalyticsOverviewResponse getPlatformOverview() {
        log.info("Generating platform analytics overview");

        Instant now = Instant.now();
        Instant lastMonth = now.minus(30, ChronoUnit.DAYS);
        Instant previousMonth = now.minus(60, ChronoUnit.DAYS);

        // Calculate metrics
        long totalUsers = userRepository.count();
        long totalVendors = vendorRepository.count();
        long totalOrders = orderRepository.count();

        // Calculate growth
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(lastMonth);
        long newUsersPrevMonth = userRepository.countByCreatedAtAfter(previousMonth) - newUsersThisMonth;
        double userGrowth = calculateGrowthPercentage(newUsersPrevMonth, newUsersThisMonth);

        long newVendorsThisMonth = vendorRepository.countByCreatedAtAfter(lastMonth);
        double vendorGrowth = calculateGrowthPercentage(0, newVendorsThisMonth);

        long ordersThisMonth = orderRepository.countByCreatedAtAfter(lastMonth);
        double orderGrowth = calculateGrowthPercentage(0, ordersThisMonth);

        return AnalyticsOverviewResponse.builder()
                .platformMetrics(PlatformMetrics.builder()
                        .totalUsers(totalUsers)
                        .totalVendors(totalVendors)
                        .totalOrders(totalOrders)
                        .totalRevenue(BigDecimal.ZERO) // TODO: Calculate from transactions
                        .platformEarnings(BigDecimal.ZERO)
                        .averageOrderValue(0)
                        .conversionRate(0)
                        .build())
                .growthMetrics(GrowthMetrics.builder()
                        .userGrowthPercentage(userGrowth)
                        .vendorGrowthPercentage(vendorGrowth)
                        .orderGrowthPercentage(orderGrowth)
                        .revenueGrowthPercentage(0)
                        .build())
                .topVendors(getTopVendors(5))
                .ordersByStatus(getOrdersByStatus())
                .revenueChart(getRevenueChart(30))
                .build();
    }

    public Map<String, Object> getRevenueAnalytics(String period) {
        log.info("Generating revenue analytics for period: {}", period);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("period", period);
        analytics.put("totalRevenue", BigDecimal.ZERO);
        analytics.put("platformFees", BigDecimal.ZERO);
        analytics.put("vendorPayouts", BigDecimal.ZERO);
        analytics.put("refunds", BigDecimal.ZERO);
        analytics.put("pendingPayments", BigDecimal.ZERO);

        return analytics;
    }

    public Map<String, Object> getUserAnalytics() {
        log.info("Generating user analytics");

        Instant now = Instant.now();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", userRepository.count());
        analytics.put("activeUsers", userRepository.countByStatus(
                com.cateringmarketplace.module.auth.model.enums.UserStatus.ACTIVE));
        analytics.put("newUsersToday", userRepository.countByCreatedAtAfter(now.truncatedTo(ChronoUnit.DAYS)));
        analytics.put("newUsersThisWeek", userRepository.countByCreatedAtAfter(now.minus(7, ChronoUnit.DAYS)));
        analytics.put("newUsersThisMonth", userRepository.countByCreatedAtAfter(now.minus(30, ChronoUnit.DAYS)));

        return analytics;
    }

    public Map<String, Object> getVendorAnalytics() {
        log.info("Generating vendor analytics");

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalVendors", vendorRepository.count());
        analytics.put("activeVendors", vendorRepository.countByStatus(Vendor.VendorStatus.ACTIVE));
        analytics.put("pendingApproval", vendorRepository.countByApprovalStatus(Vendor.ApprovalStatus.PENDING));
        analytics.put("verifiedVendors", vendorRepository.countByVerifiedTrue());

        return analytics;
    }

    public Map<String, Object> getOrderAnalytics(String period) {
        log.info("Generating order analytics for period: {}", period);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOrders", orderRepository.count());
        analytics.put("ordersByStatus", getOrdersByStatus());
        analytics.put("averageOrderValue", BigDecimal.ZERO);

        return analytics;
    }

    // ==================== VENDOR ANALYTICS ====================

    public VendorDashboardResponse getVendorDashboard(String vendorId) {
        log.info("Generating vendor dashboard for: {}", vendorId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        long totalOrders = orderRepository.countByVendorId(vendorId);
        long completedOrders = 0; // TODO: Count by status for vendor
        long totalBids = vendorBidRepository.countByVendorId(vendorId);

        return VendorDashboardResponse.builder()
                .vendorId(vendorId)
                .vendorName(vendor.getBusinessName())
                .metrics(VendorMetrics.builder()
                        .totalOrders(totalOrders)
                        .completedOrders(completedOrders)
                        .pendingOrders(0)
                        .cancelledOrders(0)
                        .totalRevenue(BigDecimal.ZERO)
                        .pendingPayouts(BigDecimal.ZERO)
                        .thisMonthRevenue(BigDecimal.ZERO)
                        .build())
                .bidMetrics(BidMetrics.builder()
                        .totalBidsSubmitted(totalBids)
                        .acceptedBids(0)
                        .pendingBids(0)
                        .acceptanceRate(0)
                        .averageBidAmount(0)
                        .build())
                .recentOrders(new ArrayList<>())
                .upcomingEvents(new ArrayList<>())
                .performance(PerformanceMetrics.builder()
                        .averageRating(vendor.getRatings() != null ?
                                vendor.getRatings().getAverageRating().doubleValue() : 0)
                        .totalReviews(vendor.getRatings() != null ?
                                vendor.getRatings().getTotalReviews() : 0)
                        .responseRate(0)
                        .onTimeDeliveryRate(0)
                        .repeatCustomers(0)
                        .build())
                .build();
    }

    // ==================== REPORTS ====================

    public Map<String, Object> generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        log.info("Generating {} report from {} to {}", reportType, startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", reportType);
        report.put("startDate", startDate.toString());
        report.put("endDate", endDate.toString());
        report.put("generatedAt", Instant.now().toString());

        switch (reportType.toUpperCase()) {
            case "REVENUE" -> report.put("data", getRevenueAnalytics("custom"));
            case "ORDERS" -> report.put("data", getOrderAnalytics("custom"));
            case "USERS" -> report.put("data", getUserAnalytics());
            case "VENDORS" -> report.put("data", getVendorAnalytics());
            default -> report.put("data", new HashMap<>());
        }

        return report;
    }

    // ==================== HELPER METHODS ====================

    private double calculateGrowthPercentage(long previous, long current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100;
    }

    private List<TopVendor> getTopVendors(int limit) {
        // Simplified - in production, use aggregation
        return vendorRepository.findAll(PageRequest.of(0, limit))
                .stream()
                .map(v -> TopVendor.builder()
                        .vendorId(v.getVendorId())
                        .vendorName(v.getBusinessName())
                        .totalOrders(v.getStats() != null ? v.getStats().getTotalOrders() : 0)
                        .totalRevenue(BigDecimal.ZERO)
                        .rating(v.getRatings() != null ? v.getRatings().getAverageRating().doubleValue() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getOrdersByStatus() {
        Map<String, Long> statusCounts = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            statusCounts.put(status.name(), orderRepository.countByStatus(status));
        }
        return statusCounts;
    }

    private List<RevenueDataPoint> getRevenueChart(int days) {
        // Simplified - return empty for now
        return new ArrayList<>();
    }
}

