package com.cateringmarketplace.module.analytics.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.analytics.dto.response.AnalyticsOverviewResponse;
import com.cateringmarketplace.module.analytics.dto.response.VendorDashboardResponse;
import com.cateringmarketplace.module.analytics.service.AnalyticsService;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller for analytics operations.
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Analytics and reporting APIs")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final VendorService vendorService;

    // ==================== ADMIN ANALYTICS ====================

    @GetMapping("/overview")
    @Operation(summary = "Get platform overview", description = "Returns platform-wide analytics overview (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> getPlatformOverview() {
        AnalyticsOverviewResponse overview = analyticsService.getPlatformOverview();
        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue analytics", description = "Returns revenue analytics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueAnalytics(
            @RequestParam(defaultValue = "month") String period) {
        Map<String, Object> analytics = analyticsService.getRevenueAnalytics(period);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/users")
    @Operation(summary = "Get user analytics", description = "Returns user analytics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAnalytics() {
        Map<String, Object> analytics = analyticsService.getUserAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/vendors")
    @Operation(summary = "Get vendor analytics", description = "Returns vendor analytics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorAnalytics() {
        Map<String, Object> analytics = analyticsService.getVendorAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get order analytics", description = "Returns order analytics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderAnalytics(
            @RequestParam(defaultValue = "month") String period) {
        Map<String, Object> analytics = analyticsService.getOrderAnalytics(period);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/reports")
    @Operation(summary = "Generate report", description = "Generates custom analytics report (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateReport(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Generating {} report from {} to {}", reportType, startDate, endDate);
        Map<String, Object> report = analyticsService.generateReport(reportType, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    // ==================== VENDOR ANALYTICS ====================

    @GetMapping("/vendor/dashboard")
    @Operation(summary = "Get vendor dashboard", description = "Returns vendor dashboard analytics (Vendor only)")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorDashboardResponse>> getVendorDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        VendorDashboardResponse dashboard = analyticsService.getVendorDashboard(vendorId);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}

