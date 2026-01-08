package com.cateringmarketplace.module.admin.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.admin.dto.request.CreateAnnouncementRequest;
import com.cateringmarketplace.module.admin.dto.request.UpdatePlatformConfigRequest;
import com.cateringmarketplace.module.admin.dto.response.DashboardStatsResponse;
import com.cateringmarketplace.module.admin.model.Announcement;
import com.cateringmarketplace.module.admin.model.AuditLog;
import com.cateringmarketplace.module.admin.model.PlatformConfig;
import com.cateringmarketplace.module.admin.service.AdminService;
import com.cateringmarketplace.module.auth.dto.response.UserResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.order.dto.response.OrderResponse;
import com.cateringmarketplace.module.order.service.OrderService;
import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for admin operations.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final VendorService vendorService;
    private final OrderService orderService;

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats", description = "Returns admin dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Returns paginated list of all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> users = adminService.getAllUsers(pageable, status, userType);

        return ResponseEntity.ok(ApiResponse.success(
                users.getContent(),
                "Users retrieved",
                PageInfo.from(users)
        ));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Update user status", description = "Updates user status (activate/suspend)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam String status,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} updating user {} status to {}", userDetails.getUserId(), userId, status);
        UserResponse response = adminService.updateUserStatus(userId, status, userDetails.getUserId(), reason);
        return ResponseEntity.ok(ApiResponse.success(response, "User status updated"));
    }

    // ==================== VENDOR MANAGEMENT ====================

    @GetMapping("/vendors/pending")
    @Operation(summary = "Get pending vendors", description = "Returns vendors pending approval")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getPendingVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<VendorResponse> vendors = vendorService.getPendingVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                vendors.getContent(),
                "Pending vendors retrieved",
                PageInfo.from(vendors)
        ));
    }

    @PostMapping("/vendors/{vendorId}/approve")
    @Operation(summary = "Approve vendor", description = "Approves a vendor registration")
    public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(
            @PathVariable String vendorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} approving vendor {}", userDetails.getUserId(), vendorId);
        VendorResponse response = vendorService.approveVendor(vendorId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor approved successfully"));
    }

    @PostMapping("/vendors/{vendorId}/reject")
    @Operation(summary = "Reject vendor", description = "Rejects a vendor registration with reason")
    public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(
            @PathVariable String vendorId,
            @RequestParam String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} rejecting vendor {} with reason: {}", userDetails.getUserId(), vendorId, reason);
        VendorResponse response = vendorService.rejectVendor(vendorId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor rejected"));
    }

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    @Operation(summary = "Get all orders", description = "Returns all platform orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orders = orderService.getAllOrders(pageable, status);

        return ResponseEntity.ok(ApiResponse.success(
                orders.getContent(),
                "Orders retrieved",
                PageInfo.from(orders)
        ));
    }

    // ==================== PLATFORM CONFIG ====================

    @GetMapping("/platform-config")
    @Operation(summary = "Get platform config", description = "Returns platform configuration")
    public ResponseEntity<ApiResponse<PlatformConfig>> getPlatformConfig(
            @RequestParam(defaultValue = "India") String country) {
        PlatformConfig config = adminService.getPlatformConfig(country);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @PutMapping("/platform-config")
    @Operation(summary = "Update platform config", description = "Updates platform configuration")
    public ResponseEntity<ApiResponse<PlatformConfig>> updatePlatformConfig(
            @RequestParam(defaultValue = "India") String country,
            @Valid @RequestBody UpdatePlatformConfigRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} updating platform config", userDetails.getUserId());
        PlatformConfig config = adminService.updatePlatformConfig(country, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(config, "Configuration updated"));
    }

    // ==================== AUDIT LOGS ====================

    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit logs", description = "Returns audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logs = adminService.getAuditLogs(pageable, entityType, action);

        return ResponseEntity.ok(ApiResponse.success(
                logs.getContent(),
                "Audit logs retrieved",
                PageInfo.from(logs)
        ));
    }

    // ==================== ANNOUNCEMENTS ====================

    @GetMapping("/announcements")
    @Operation(summary = "Get announcements", description = "Returns all announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Announcement> announcements = adminService.getAnnouncements(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                announcements.getContent(),
                "Announcements retrieved",
                PageInfo.from(announcements)
        ));
    }

    @PostMapping("/announcements")
    @Operation(summary = "Create announcement", description = "Creates a new platform announcement")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} creating announcement", userDetails.getUserId());
        Announcement announcement = adminService.createAnnouncement(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(announcement, "Announcement created"));
    }

    @DeleteMapping("/announcements/{announcementId}")
    @Operation(summary = "Delete announcement", description = "Deletes an announcement")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(
            @PathVariable String announcementId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteAnnouncement(announcementId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Announcement deleted"));
    }
}

