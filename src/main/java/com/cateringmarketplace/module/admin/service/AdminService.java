package com.cateringmarketplace.module.admin.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
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
import com.cateringmarketplace.module.auth.dto.request.RegisterRequest;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.NotificationPreferences;
import com.cateringmarketplace.module.auth.model.enums.UserStatus;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.payment.repository.TransactionRepository;
import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
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
        if (request.getCancellationPolicy() != null) {
            updateCancellationPolicy(config, request.getCancellationPolicy());
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
        if (dto.getPaymentTimeoutHours() != null) pc.setPaymentTimeoutHours(dto.getPaymentTimeoutHours());
        if (dto.getAutoRefundEnabled() != null) pc.setAutoRefundEnabled(dto.getAutoRefundEnabled());

        config.setPaymentConfig(pc);
    }

    private void updateCancellationPolicy(PlatformConfig config, UpdatePlatformConfigRequest.CancellationPolicyDTO dto) {
        PlatformConfig.CancellationPolicy cp = config.getCancellationPolicy();
        if (cp == null) cp = new PlatformConfig.CancellationPolicy();

        if (dto.getCancellationWindowDays() != null) cp.setCancellationWindowDays(dto.getCancellationWindowDays());
        if (dto.getRefundTiers() != null && !dto.getRefundTiers().isEmpty()) {
            List<PlatformConfig.RefundTier> refundTiers = dto.getRefundTiers().stream()
                    .map(tier -> PlatformConfig.RefundTier.builder()
                            .daysBeforeEvent(tier.getDaysBeforeEvent())
                            .refundPercentage(tier.getRefundPercentage())
                            .build())
                    .toList();
            cp.setRefundTiers(refundTiers);
        }

        config.setCancellationPolicy(cp);
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

    // ==================== SUPPORT AGENT MANAGEMENT ====================

    /**
     * Creates a new support agent account (Admin only)
     */
    @Transactional
    public UserResponse createSupportAgent(RegisterRequest request, String adminId) {
        log.info("Admin {} creating support agent with email: {}", adminId, request.getEmail());

        // Validate userType is SUPPORT_AGENT
        if (request.getUserType() == null || !request.getUserType().equalsIgnoreCase("SUPPORT_AGENT")) {
            throw new BadRequestException("INVALID_USER_TYPE", "User type must be SUPPORT_AGENT");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("EMAIL_EXISTS", "Email is already registered");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("PHONE_EXISTS", "Phone number is already registered");
        }

        // Determine preferred currency based on country
        String currency = "USD";
        if ("INDIA".equalsIgnoreCase(request.getCountry())) {
            currency = "INR";
        }

        // Create support agent user
        User agent = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .phone(request.getPhone())
                .passwordHash(new com.cateringmarketplace.common.util.PasswordUtil().hashPassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .country(request.getCountry())
                .preferredCurrency(currency)
                .userType(UserType.SUPPORT_AGENT)
                .status(UserStatus.ACTIVE)
                .notificationPreferences(NotificationPreferences.defaults())
                .build();

        agent = userRepository.save(agent);
        log.info("Support agent created successfully with ID: {}", agent.getUserId());

        // Log the action
        Map<String, Object> changes = new java.util.HashMap<>();
        changes.put("firstName", agent.getFirstName());
        changes.put("lastName", agent.getLastName());
        changes.put("email", agent.getEmail());
        createAuditLog("USER", agent.getUserId(), "CREATE_SUPPORT_AGENT", adminId, "ADMIN", changes);

        return UserResponse.fromEntity(agent);
    }

    // ==================== GENERIC STATUS MANAGEMENT (Users, Vendors, Agents) ====================

    /**
     * Suspends an active entity (User, Vendor, or Support Agent) by admin.
     * entityType: "users", "vendors", or "agents"
     */
    @Transactional
    public Object suspendEntity(String entityType, String entityId, String reason, String adminId) {
        log.info("Suspending {} {} by admin: {}", entityType, entityId, adminId);

        if ("users".equalsIgnoreCase(entityType)) {
            return suspendUserEntity(entityId, reason, adminId);
        } else if ("vendors".equalsIgnoreCase(entityType)) {
            return suspendVendorEntity(entityId, reason, adminId);
        } else if ("agents".equalsIgnoreCase(entityType)) {
            return suspendAgentEntity(entityId, reason, adminId);
        } else {
            throw new BadRequestException("INVALID_ENTITY_TYPE", "Valid entity types are: users, vendors, agents");
        }
    }

    /**
     * Activates a suspended entity (User, Vendor, or Support Agent) by admin.
     */
    @Transactional
    public Object activateEntity(String entityType, String entityId, String adminId) {
        log.info("Activating {} {} by admin: {}", entityType, entityId, adminId);

        if ("users".equalsIgnoreCase(entityType)) {
            return activateUserEntity(entityId, adminId);
        } else if ("vendors".equalsIgnoreCase(entityType)) {
            return activateVendorEntity(entityId, adminId);
        } else if ("agents".equalsIgnoreCase(entityType)) {
            return activateAgentEntity(entityId, adminId);
        } else {
            throw new BadRequestException("INVALID_ENTITY_TYPE", "Valid entity types are: users, vendors, agents");
        }
    }

    /**
     * Unlocks a locked entity (User, Vendor, or Support Agent) by admin.
     */
    @Transactional
    public Object unlockEntity(String entityType, String entityId, String adminId) {
        log.info("Unlocking {} {} by admin: {}", entityType, entityId, adminId);

        if ("users".equalsIgnoreCase(entityType)) {
            return unlockUserEntity(entityId, adminId);
        } else if ("vendors".equalsIgnoreCase(entityType)) {
            return unlockVendorEntity(entityId, adminId);
        } else if ("agents".equalsIgnoreCase(entityType)) {
            return unlockAgentEntity(entityId, adminId);
        } else {
            throw new BadRequestException("INVALID_ENTITY_TYPE", "Valid entity types are: users, vendors, agents");
        }
    }

    // ==================== USER ENTITY OPERATIONS ====================

    private UserResponse suspendUserEntity(String userId, String reason, String adminId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BadRequestException("ALREADY_SUSPENDED", "User is already suspended");
        }
        user.setStatus(UserStatus.SUSPENDED);
        user = userRepository.save(user);
        Map<String, Object> changes = new java.util.HashMap<>();
        changes.put("reason", reason);
        createAuditLog("USER", userId, "SUSPEND", adminId, "ADMIN", changes);
        return UserResponse.fromEntity(user);
    }

    private UserResponse activateUserEntity(String userId, String adminId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new BadRequestException("ALREADY_ACTIVE", "User is already active");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user = userRepository.save(user);
        createAuditLog("USER", userId, "ACTIVATE", adminId, "ADMIN", null);
        return UserResponse.fromEntity(user);
    }

    private UserResponse unlockUserEntity(String userId, String adminId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        if (user.getStatus() != UserStatus.ACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
        }
        user = userRepository.save(user);
        createAuditLog("USER", userId, "UNLOCK", adminId, "ADMIN", null);
        return UserResponse.fromEntity(user);
    }

    // ==================== SUPPORT AGENT ENTITY OPERATIONS ====================

    private UserResponse suspendAgentEntity(String agentId, String reason, String adminId) {
        User agent = userRepository.findByUserId(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Support agent not found"));
        if (agent.getUserType() != UserType.SUPPORT_AGENT) {
            throw new BadRequestException("NOT_AGENT", "User is not a support agent");
        }
        if (agent.getStatus() == UserStatus.SUSPENDED) {
            throw new BadRequestException("ALREADY_SUSPENDED", "Support agent is already suspended");
        }
        agent.setStatus(UserStatus.SUSPENDED);
        agent = userRepository.save(agent);
        Map<String, Object> changes = new java.util.HashMap<>();
        changes.put("reason", reason);
        createAuditLog("SUPPORT_AGENT", agentId, "SUSPEND", adminId, "ADMIN", changes);
        return UserResponse.fromEntity(agent);
    }

    private UserResponse activateAgentEntity(String agentId, String adminId) {
        User agent = userRepository.findByUserId(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Support agent not found"));
        if (agent.getUserType() != UserType.SUPPORT_AGENT) {
            throw new BadRequestException("NOT_AGENT", "User is not a support agent");
        }
        if (agent.getStatus() == UserStatus.ACTIVE) {
            throw new BadRequestException("ALREADY_ACTIVE", "Support agent is already active");
        }
        agent.setStatus(UserStatus.ACTIVE);
        agent.setFailedLoginAttempts(0);
        agent = userRepository.save(agent);
        createAuditLog("SUPPORT_AGENT", agentId, "ACTIVATE", adminId, "ADMIN", null);
        return UserResponse.fromEntity(agent);
    }

    private UserResponse unlockAgentEntity(String agentId, String adminId) {
        User agent = userRepository.findByUserId(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Support agent not found"));
        if (agent.getUserType() != UserType.SUPPORT_AGENT) {
            throw new BadRequestException("NOT_AGENT", "User is not a support agent");
        }
        agent.setFailedLoginAttempts(0);
        agent.setLockedUntil(null);
        if (agent.getStatus() != UserStatus.ACTIVE) {
            agent.setStatus(UserStatus.ACTIVE);
        }
        agent = userRepository.save(agent);
        createAuditLog("SUPPORT_AGENT", agentId, "UNLOCK", adminId, "ADMIN", null);
        return UserResponse.fromEntity(agent);
    }

    // ==================== VENDOR ENTITY OPERATIONS ====================

    private VendorResponse suspendVendorEntity(String vendorId, String reason, String adminId) {
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        if (vendor.getStatus() == Vendor.VendorStatus.SUSPENDED) {
            throw new BadRequestException("ALREADY_SUSPENDED", "Vendor is already suspended");
        }
        vendor.setStatus(Vendor.VendorStatus.SUSPENDED);
        vendor = vendorRepository.save(vendor);
        Map<String, Object> changes = new java.util.HashMap<>();
        changes.put("reason", reason);
        createAuditLog("VENDOR", vendorId, "SUSPEND", adminId, "ADMIN", changes);
        return VendorResponse.fromEntity(vendor);
    }

    private VendorResponse activateVendorEntity(String vendorId, String adminId) {
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        if (vendor.getStatus() == Vendor.VendorStatus.ACTIVE) {
            throw new BadRequestException("ALREADY_ACTIVE", "Vendor is already active");
        }
        vendor.setStatus(Vendor.VendorStatus.ACTIVE);
        vendor = vendorRepository.save(vendor);
        createAuditLog("VENDOR", vendorId, "ACTIVATE", adminId, "ADMIN", null);
        return VendorResponse.fromEntity(vendor);
    }

    private VendorResponse unlockVendorEntity(String vendorId, String adminId) {
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        if (vendor.getStatus() != Vendor.VendorStatus.ACTIVE) {
            vendor.setStatus(Vendor.VendorStatus.ACTIVE);
        }
        vendor = vendorRepository.save(vendor);
        createAuditLog("VENDOR", vendorId, "UNLOCK", adminId, "ADMIN", null);
        return VendorResponse.fromEntity(vendor);
    }
}

