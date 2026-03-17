package com.cateringmarketplace.module.admin.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.admin.dto.request.ChangeAnnouncementStatusRequest;
import com.cateringmarketplace.module.admin.dto.request.CreateAnnouncementRequest;
import com.cateringmarketplace.module.admin.dto.request.UpdateAnnouncementRequest;
import com.cateringmarketplace.module.admin.dto.request.UpdatePlatformConfigRequest;
import com.cateringmarketplace.module.admin.dto.response.AnnouncementResponse;
import com.cateringmarketplace.module.admin.dto.response.DashboardStatsResponse;
import com.cateringmarketplace.module.admin.model.Announcement;
import com.cateringmarketplace.module.admin.model.AuditLog;
import com.cateringmarketplace.module.admin.model.PlatformConfig;
import com.cateringmarketplace.module.admin.service.AdminService;
import com.cateringmarketplace.module.auth.dto.request.RegisterRequest;
import com.cateringmarketplace.module.auth.dto.response.UserResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.bid.dto.response.BidRequestResponse;
import com.cateringmarketplace.module.bid.service.BidService;
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
    private final BidService bidService;

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

    // ==================== GENERIC STATUS MANAGEMENT (Users, Vendors, Agents) ====================

    @PatchMapping("/{entityType}/{entityId}/suspend")
    @Operation(summary = "Suspend entity", description = "Suspends an active user, vendor, or support agent")
    public ResponseEntity<ApiResponse<?>> suspendEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} suspending {} {} with reason: {}", userDetails.getUserId(), entityType, entityId, reason);
        Object response = adminService.suspendEntity(entityType, entityId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, entityType.substring(0, 1).toUpperCase() + entityType.substring(1) + " suspended"));
    }

    @PatchMapping("/{entityType}/{entityId}/activate")
    @Operation(summary = "Activate entity", description = "Activates a suspended user, vendor, or support agent")
    public ResponseEntity<ApiResponse<?>> activateEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} activating {} {}", userDetails.getUserId(), entityType, entityId);
        Object response = adminService.activateEntity(entityType, entityId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, entityType.substring(0, 1).toUpperCase() + entityType.substring(1) + " activated"));
    }

    @PatchMapping("/{entityType}/{entityId}/unlock")
    @Operation(summary = "Unlock entity", description = "Unlocks a locked user, vendor, or support agent")
    public ResponseEntity<ApiResponse<?>> unlockEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} unlocking {} {}", userDetails.getUserId(), entityType, entityId);
        Object response = adminService.unlockEntity(entityType, entityId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, entityType.substring(0, 1).toUpperCase() + entityType.substring(1) + " unlocked"));
    }

    // ==================== VENDOR MANAGEMENT ====================

    @GetMapping("/vendors")
    @Operation(summary = "Get all vendors", description = "Returns paginated list of all vendors with filtering")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VendorResponse> vendors = vendorService.getAllVendorsFiltered(pageable, approvalStatus, status, country, search);

        return ResponseEntity.ok(ApiResponse.success(
                vendors.getContent(),
                "Vendors retrieved",
                PageInfo.from(vendors)
        ));
    }

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

    @PutMapping("/vendors/{vendorId}/approve")
    @Operation(summary = "Approve vendor", description = "Approves a vendor registration")
    public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(
            @PathVariable String vendorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} approving vendor {}", userDetails.getUserId(), vendorId);
        VendorResponse response = vendorService.approveVendor(vendorId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor approved successfully"));
    }

    @PutMapping("/vendors/{vendorId}/reject")
    @Operation(summary = "Reject vendor", description = "Rejects a vendor registration with reason")
    public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(
            @PathVariable String vendorId,
            @RequestBody java.util.Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String reason = body.getOrDefault("reason", "No reason provided");
        log.info("Admin {} rejecting vendor {} with reason: {}", userDetails.getUserId(), vendorId, reason);
        VendorResponse response = vendorService.rejectVendor(vendorId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor rejected"));
    }

    @PatchMapping("/vendors/{vendorId}/suspend")
    @Operation(summary = "Suspend vendor", description = "Suspends an active vendor account")
    public ResponseEntity<ApiResponse<VendorResponse>> suspendVendor(
            @PathVariable String vendorId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} suspending vendor {} with reason: {}", userDetails.getUserId(), vendorId, reason);
        VendorResponse response = vendorService.suspendVendor(vendorId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor suspended"));
    }

    @PatchMapping("/vendors/{vendorId}/activate")
    @Operation(summary = "Activate vendor", description = "Activates a suspended or inactive vendor")
    public ResponseEntity<ApiResponse<VendorResponse>> activateVendor(
            @PathVariable String vendorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} activating vendor {}", userDetails.getUserId(), vendorId);
        VendorResponse response = vendorService.activateVendor(vendorId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor activated"));
    }

    @PatchMapping("/vendors/{vendorId}/unlock")
    @Operation(summary = "Unlock vendor", description = "Unlocks a locked vendor account")
    public ResponseEntity<ApiResponse<VendorResponse>> unlockVendor(
            @PathVariable String vendorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} unlocking vendor {}", userDetails.getUserId(), vendorId);
        VendorResponse response = vendorService.unlockVendor(vendorId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor unlocked"));
    }

    // ==================== BID MANAGEMENT ====================

    @GetMapping("/bids")
    @Operation(summary = "Get all bid requests", description = "Returns paginated list of all bid requests with optional status filter")
    public ResponseEntity<ApiResponse<List<BidRequestResponse>>> getAllBids(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BidRequestResponse> bids = bidService.getAllBidsAdmin(pageable, status);

        return ResponseEntity.ok(ApiResponse.success(
                bids.getContent(),
                "Bid requests retrieved",
                PageInfo.from(bids)
        ));
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

    @GetMapping("/platform-config/{country}")
    @Operation(summary = "Get platform config by country", description = "Returns platform configuration for a specific country")
    public ResponseEntity<ApiResponse<PlatformConfig>> getPlatformConfigByCountry(
            @PathVariable String country) {
        log.info("Fetching platform config for country: {}", country);
        PlatformConfig config = adminService.getPlatformConfig(country);
        return ResponseEntity.ok(ApiResponse.success(config, "Configuration retrieved"));
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

    @PutMapping("/platform-config/{country}")
    @Operation(summary = "Update platform config by country", description = "Updates platform configuration for a specific country")
    public ResponseEntity<ApiResponse<PlatformConfig>> updatePlatformConfigByCountry(
            @PathVariable String country,
            @Valid @RequestBody UpdatePlatformConfigRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} updating platform config for country: {}", userDetails.getUserId(), country);
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
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AnnouncementResponse> announcements = adminService.getAnnouncements(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                announcements.getContent(),
                "Announcements retrieved",
                PageInfo.from(announcements)
        ));
    }

    @PostMapping("/announcements")
    @Operation(summary = "Create announcement", description = "Creates a new platform announcement")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} creating announcement", userDetails.getUserId());
        AnnouncementResponse announcement = adminService.createAnnouncement(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(announcement, "Announcement created"));
    }

    @DeleteMapping("/announcements/{announcementId}")
    @Operation(summary = "Delete announcement", description = "Deletes an announcement from database")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(
            @PathVariable String announcementId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteAnnouncement(announcementId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Announcement deleted"));
    }

    @GetMapping("/announcements/{announcementId}")
    @Operation(summary = "Get announcement by ID", description = "Returns a specific announcement by ID")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getAnnouncementById(
            @PathVariable String announcementId) {
        log.info("Getting announcement: {}", announcementId);
        AnnouncementResponse announcement = adminService.getAnnouncementById(announcementId);
        return ResponseEntity.ok(ApiResponse.success(announcement, "Announcement retrieved"));
    }

    @PutMapping("/announcements/{announcementId}")
    @Operation(summary = "Update announcement", description = "Updates an existing announcement")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @PathVariable String announcementId,
            @Valid @RequestBody UpdateAnnouncementRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} updating announcement {}", userDetails.getUserId(), announcementId);
        AnnouncementResponse announcement = adminService.updateAnnouncement(announcementId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(announcement, "Announcement updated"));
    }

    @PatchMapping("/announcements/{announcementId}/status")
    @Operation(summary = "Change announcement status", description = "Changes announcement active/inactive status")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> changeAnnouncementStatus(
            @PathVariable String announcementId,
            @Valid @RequestBody ChangeAnnouncementStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} changing announcement {} status to: {}", userDetails.getUserId(), announcementId, request.getIsActive());
        AnnouncementResponse announcement = adminService.changeAnnouncementStatus(announcementId, request.getIsActive(), userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(announcement, "Announcement status changed"));
    }

    // ==================== SUPPORT AGENTS MANAGEMENT ====================

    @GetMapping("/agents")
    @Operation(summary = "Get all support agents", description = "Returns paginated list of all support agents")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllSupportAgents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Filter by userType SUPPORT_AGENT
        Page<UserResponse> agents = adminService.getAllUsers(pageable, status, "SUPPORT_AGENT");

        return ResponseEntity.ok(ApiResponse.success(
                agents.getContent(),
                "Support agents retrieved",
                PageInfo.from(agents)
        ));
    }

    @GetMapping("/support-agents")
    @Operation(summary = "Get all support agents (alternative endpoint)", description = "Returns paginated list of all support agents - alternative naming for /agents")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllSupportAgentsAlt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Filter by userType SUPPORT_AGENT
        Page<UserResponse> agents = adminService.getAllUsers(pageable, status, "SUPPORT_AGENT");

        return ResponseEntity.ok(ApiResponse.success(
                agents.getContent(),
                "Support agents retrieved",
                PageInfo.from(agents)
        ));
    }

    @PostMapping("/agents")
    @Operation(summary = "Create support agent", description = "Creates a new support agent account")
    public ResponseEntity<ApiResponse<UserResponse>> createSupportAgent(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} creating new support agent: {}", userDetails.getUserId(), request.getEmail());
        // Set userType to SUPPORT_AGENT
        request.setUserType("SUPPORT_AGENT");
        // Register the agent (without sending welcome email to support email)
        UserResponse response = adminService.createSupportAgent(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Support agent created successfully"));
    }

    @GetMapping("/agents/{agentId}")
    @Operation(summary = "Get support agent details", description = "Returns details of a specific support agent")
    public ResponseEntity<ApiResponse<UserResponse>> getSupportAgent(
            @PathVariable String agentId) {
        log.info("Getting support agent details: {}", agentId);
        UserResponse agent = adminService.getSupportAgent(agentId);
        return ResponseEntity.ok(ApiResponse.success(agent, "Support agent retrieved"));
    }

    @PutMapping("/agents/{agentId}")
    @Operation(summary = "Update support agent", description = "Updates support agent details")
    public ResponseEntity<ApiResponse<UserResponse>> updateSupportAgent(
            @PathVariable String agentId,
            @RequestBody UserResponse request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} updating support agent: {}", userDetails.getUserId(), agentId);
        UserResponse response = adminService.updateSupportAgent(agentId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Support agent updated"));
    }

    @GetMapping("/agents/{agentId}/workload")
    @Operation(summary = "Get agent workload", description = "Returns workload statistics for a support agent")
    public ResponseEntity<ApiResponse<?>> getAgentWorkload(
            @PathVariable String agentId) {
        log.info("Getting workload for agent: {}", agentId);
        Object workload = adminService.getAgentWorkload(agentId);
        return ResponseEntity.ok(ApiResponse.success(workload, "Agent workload retrieved"));
    }
}

