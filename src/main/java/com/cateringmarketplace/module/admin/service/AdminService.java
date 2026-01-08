package com.cateringmarketplace.module.admin.service;

import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.admin.dto.request.CreateAnnouncementRequest;
import com.cateringmarketplace.module.admin.dto.request.UpdatePlatformConfigRequest;
import com.cateringmarketplace.module.admin.dto.response.DashboardStatsResponse;
import com.cateringmarketplace.module.admin.dto.response.DashboardStatsResponse.*;
import com.cateringmarketplace.module.admin.model.Announcement;
import com.cateringmarketplace.module.admin.model.Announcement.AnnouncementPriority;
import com.cateringmarketplace.module.admin.model.Announcement.TargetAudience;
import com.cateringmarketplace.module.admin.model.AuditLog;
import com.cateringmarketplace.module.admin.model.PlatformConfig;
import com.cateringmarketplace.module.admin.repository.AnnouncementRepository;
import com.cateringmarketplace.module.admin.repository.AuditLogRepository;
import com.cateringmarketplace.module.admin.repository.PlatformConfigRepository;
import com.cateringmarketplace.module.auth.dto.response.UserResponse;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.payment.repository.TransactionRepository;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.model.Vendor.ApprovalStatus;
import com.cateringmarketplace.module.vendor.model.Vendor.VendorStatus;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for admin operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final BidRequestRepository bidRequestRepository;
    private final VendorBidRepository vendorBidRepository;
    private final TransactionRepository transactionRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final AuditLogRepository auditLogRepository;
    private final AnnouncementRepository announcementRepository;

    // ==================== DASHBOARD ====================

    public DashboardStatsResponse getDashboardStats() {
        log.info("Generating admin dashboard stats");

        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        Instant startOfWeek = now.minus(7, ChronoUnit.DAYS);
        Instant startOfMonth = now.minus(30, ChronoUnit.DAYS);

        return DashboardStatsResponse.builder()
                .userStats(getUserStats(startOfDay, startOfWeek, startOfMonth))
                .vendorStats(getVendorStats(startOfMonth))
                .orderStats(getOrderStats(startOfDay, startOfWeek, startOfMonth))
                .revenueStats(getRevenueStats(startOfDay, startOfWeek, startOfMonth))
                .bidStats(getBidStats())
                .build();
    }

    private UserStats getUserStats(Instant startOfDay, Instant startOfWeek, Instant startOfMonth) {
        return UserStats.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByStatus(UserStatus.ACTIVE))
                .newUsersToday(userRepository.countByCreatedAtAfter(startOfDay))
                .newUsersThisWeek(userRepository.countByCreatedAtAfter(startOfWeek))
                .newUsersThisMonth(userRepository.countByCreatedAtAfter(startOfMonth))
                .build();
    }

    private VendorStats getVendorStats(Instant startOfMonth) {
        return VendorStats.builder()
                .totalVendors(vendorRepository.count())
                .activeVendors(vendorRepository.countByStatus(VendorStatus.ACTIVE))
                .pendingApproval(vendorRepository.countByApprovalStatus(ApprovalStatus.PENDING))
                .verifiedVendors(vendorRepository.countByVerifiedTrue())
                .newVendorsThisMonth(vendorRepository.countByCreatedAtAfter(startOfMonth))
                .build();
    }

    private OrderStats getOrderStats(Instant startOfDay, Instant startOfWeek, Instant startOfMonth) {
        return OrderStats.builder()
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(Order.OrderStatus.CONFIRMED))
                .completedOrders(orderRepository.countByStatus(Order.OrderStatus.COMPLETED))
                .cancelledOrders(orderRepository.countByStatus(Order.OrderStatus.CANCELLED))
                .ordersToday(orderRepository.countByCreatedAtAfter(startOfDay))
                .ordersThisWeek(orderRepository.countByCreatedAtAfter(startOfWeek))
                .ordersThisMonth(orderRepository.countByCreatedAtAfter(startOfMonth))
                .build();
    }

    private RevenueStats getRevenueStats(Instant startOfDay, Instant startOfWeek, Instant startOfMonth) {
        // Simplified revenue calculation - in production, use aggregation queries
        return RevenueStats.builder()
                .totalRevenue(BigDecimal.ZERO) // TODO: Calculate from transactions
                .revenueToday(BigDecimal.ZERO)
                .revenueThisWeek(BigDecimal.ZERO)
                .revenueThisMonth(BigDecimal.ZERO)
                .platformFees(BigDecimal.ZERO)
                .pendingPayouts(BigDecimal.ZERO)
                .build();
    }

    private BidStats getBidStats() {
        long totalBidRequests = bidRequestRepository.count();
        long activeBidRequests = bidRequestRepository.countByStatus(BidRequest.BidRequestStatus.ACTIVE);
        long totalBids = vendorBidRepository.count();

        return BidStats.builder()
                .totalBidRequests(totalBidRequests)
                .activeBidRequests(activeBidRequests)
                .totalBidsSubmitted(totalBids)
                .acceptedBids(vendorBidRepository.countByStatusAccepted())
                .averageBidsPerRequest(totalBidRequests > 0 ? (double) totalBids / totalBidRequests : 0)
                .build();
    }

    // ==================== USER MANAGEMENT ====================

    public Page<UserResponse> getAllUsers(Pageable pageable, String status, String userType) {
        Page<User> users;

        if (status != null && userType != null) {
            users = userRepository.findByStatusAndUserType(
                    UserStatus.valueOf(status.toUpperCase()),
                    com.cateringmarketplace.module.auth.model.enums.UserType.valueOf(userType.toUpperCase()),
                    pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(UserStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(UserResponse::fromEntity);
    }

    @Transactional
    public UserResponse updateUserStatus(String userId, String newStatus, String adminId, String reason) {
        log.info("Admin {} updating user {} status to {}", adminId, userId, newStatus);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserStatus oldStatus = user.getStatus();
        UserStatus status = UserStatus.valueOf(newStatus.toUpperCase());
        user.setStatus(status);
        user = userRepository.save(user);

        // Create audit log
        createAuditLog("USER", userId, "STATUS_CHANGE", adminId, "ADMIN",
                Map.of("old_status", oldStatus.name(), "new_status", status.name(), "reason", reason));

        return UserResponse.fromEntity(user);
    }

    // ==================== PLATFORM CONFIG ====================

    public PlatformConfig getPlatformConfig(String country) {
        return platformConfigRepository.findByCountry(country)
                .orElseGet(() -> {
                    PlatformConfig config = PlatformConfig.createDefault(country);
                    config.setConfigId(UUID.randomUUID().toString());
                    return platformConfigRepository.save(config);
                });
    }

    @Transactional
    public PlatformConfig updatePlatformConfig(String country, UpdatePlatformConfigRequest request, String adminId) {
        log.info("Admin {} updating platform config for country {}", adminId, country);

        PlatformConfig config = getPlatformConfig(country);

        if (request.getBiddingConfig() != null) {
            updateBiddingConfig(config, request.getBiddingConfig());
        }
        if (request.getPaymentConfig() != null) {
            updatePaymentConfig(config, request.getPaymentConfig());
        }
        if (request.getCommissionConfig() != null) {
            updateCommissionConfig(config, request.getCommissionConfig());
        }

        config.setUpdatedBy(adminId);
        config.setUpdatedAt(Instant.now());

        config = platformConfigRepository.save(config);

        createAuditLog("PLATFORM_CONFIG", config.getConfigId(), "UPDATE", adminId, "ADMIN", null);

        return config;
    }

    private void updateBiddingConfig(PlatformConfig config, UpdatePlatformConfigRequest.BiddingConfigDTO dto) {
        PlatformConfig.BiddingConfig bc = config.getBiddingConfig();
        if (bc == null) bc = new PlatformConfig.BiddingConfig();

        if (dto.getCompetitivePeriodHours() != null) bc.setCompetitivePeriodHours(dto.getCompetitivePeriodHours());
        if (dto.getCoolingPeriodHours() != null) bc.setCoolingPeriodHours(dto.getCoolingPeriodHours());
        if (dto.getBidExpiryHours() != null) bc.setBidExpiryHours(dto.getBidExpiryHours());
        if (dto.getMaxBidRevisions() != null) bc.setMaxBidRevisions(dto.getMaxBidRevisions());

        config.setBiddingConfig(bc);
    }

    private void updatePaymentConfig(PlatformConfig config, UpdatePlatformConfigRequest.PaymentConfigDTO dto) {
        PlatformConfig.PaymentConfig pc = config.getPaymentConfig();
        if (pc == null) pc = new PlatformConfig.PaymentConfig();

        if (dto.getTokenPercentage() != null) pc.setTokenPercentage(dto.getTokenPercentage());
        if (dto.getEnabledGateways() != null) pc.setEnabledGateways(dto.getEnabledGateways());
        if (dto.getDefaultGateway() != null) pc.setDefaultGateway(dto.getDefaultGateway());

        config.setPaymentConfig(pc);
    }

    private void updateCommissionConfig(PlatformConfig config, UpdatePlatformConfigRequest.CommissionConfigDTO dto) {
        PlatformConfig.CommissionConfig cc = config.getCommissionConfig();
        if (cc == null) cc = new PlatformConfig.CommissionConfig();

        if (dto.getPlatformFeePercentage() != null) cc.setPlatformFeePercentage(dto.getPlatformFeePercentage());
        if (dto.getVendorCommissionPercentage() != null) cc.setVendorCommissionPercentage(dto.getVendorCommissionPercentage());

        config.setCommissionConfig(cc);
    }

    // ==================== AUDIT LOGS ====================

    public Page<AuditLog> getAuditLogs(Pageable pageable, String entityType, String action) {
        if (entityType != null && action != null) {
            return auditLogRepository.findByEntityTypeAndAction(entityType, action, pageable);
        } else if (entityType != null) {
            return auditLogRepository.findByEntityTypeOrderByTimestampDesc(entityType, pageable);
        }
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public void createAuditLog(String entityType, String entityId, String action,
                                String performedBy, String performedByType,
                                Map<String, Object> changes) {
        AuditLog log = AuditLog.builder()
                .logId(UUID.randomUUID().toString())
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .performedByType(performedByType)
                .changes(changes)
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(log);
    }

    // ==================== ANNOUNCEMENTS ====================

    public Page<Announcement> getAnnouncements(Pageable pageable) {
        return announcementRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<Announcement> getActiveAnnouncements(TargetAudience audience) {
        return announcementRepository.findActiveAnnouncementsForAudience(audience, Instant.now());
    }

    @Transactional
    public Announcement createAnnouncement(CreateAnnouncementRequest request, String adminId) {
        log.info("Admin {} creating announcement", adminId);

        Announcement announcement = Announcement.builder()
                .announcementId(UUID.randomUUID().toString())
                .title(request.getTitle())
                .message(request.getMessage())
                .targetAudience(request.getTargetAudience() != null ?
                        TargetAudience.valueOf(request.getTargetAudience().toUpperCase()) : TargetAudience.ALL)
                .priority(request.getPriority() != null ?
                        AnnouncementPriority.valueOf(request.getPriority().toUpperCase()) : AnnouncementPriority.NORMAL)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(adminId)
                .build();

        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(String announcementId, String adminId) {
        Announcement announcement = announcementRepository.findByAnnouncementId(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        announcement.setIsActive(false);
        announcementRepository.save(announcement);

        createAuditLog("ANNOUNCEMENT", announcementId, "DELETE", adminId, "ADMIN", null);
    }
}

