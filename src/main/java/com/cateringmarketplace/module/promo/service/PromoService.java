package com.cateringmarketplace.module.promo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.promo.dto.request.ApplyPromoRequest;
import com.cateringmarketplace.module.promo.dto.request.CreatePromoRequest;
import com.cateringmarketplace.module.promo.dto.response.ApplyPromoResponse;
import com.cateringmarketplace.module.promo.dto.response.PromoCodeResponse;
import com.cateringmarketplace.module.promo.model.PromoCode;
import com.cateringmarketplace.module.promo.model.PromoCode.*;
import com.cateringmarketplace.module.promo.model.PromoRedemption;
import com.cateringmarketplace.module.promo.repository.PromoCodeRepository;
import com.cateringmarketplace.module.promo.repository.PromoRedemptionRepository;

/**
 * Service class for promo code operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromoService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoRedemptionRepository promoRedemptionRepository;
    private final OrderRepository orderRepository;

    // =========================================================
    // CREATE
    // =========================================================

    /**
     * Creates a new promo code.
     */
    @Transactional
    public PromoCodeResponse createPromo(CreatePromoRequest request, String createdBy) {

        log.info("Creating promo code: {}", request.getCode());

        if (promoCodeRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new ConflictException("CODE_EXISTS", "Promo code already exists");
        }

        PromoCode promo = PromoCode.builder()
                .promoCodeId(UUID.randomUUID().toString())
                .code(request.getCode().toUpperCase())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(PromoType.valueOf(request.getType().toUpperCase()))
                .value(request.getValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .validFrom(request.getValidFrom() != null ? request.getValidFrom() : Instant.now())
                .validTo(request.getValidTo())
                .usageLimitGlobal(request.getUsageLimitGlobal())
                .usageLimitPerUser(request.getUsageLimitPerUser() != null
                        ? request.getUsageLimitPerUser() : 1)
                .applicableTo(request.getApplicableTo() != null
                        ? ApplicableTo.valueOf(request.getApplicableTo().toUpperCase())
                        : ApplicableTo.ALL)
                .applicableVendorIds(request.getApplicableVendorIds())
                .applicableUserIds(request.getApplicableUserIds())
                .applicableCuisines(request.getApplicableCuisines())
                .firstOrderOnly(request.getFirstOrderOnly() != null
                        ? request.getFirstOrderOnly() : false)
                .status(PromoStatus.ACTIVE)
                .createdBy(createdBy)
                .build();

        promo = promoCodeRepository.save(promo);
        log.info("Promo code created: {}", promo.getPromoCodeId());

        return PromoCodeResponse.fromEntity(promo);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    /**
     * Updates a promo code.
     */
    @Transactional
    public PromoCodeResponse updatePromo(String promoCodeId, CreatePromoRequest request) {

        log.info("Updating promo code: {}", promoCodeId);

        PromoCode promo = promoCodeRepository.findByPromoCodeId(promoCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        if (request.getTitle() != null) promo.setTitle(request.getTitle());
        if (request.getDescription() != null) promo.setDescription(request.getDescription());
        if (request.getValue() != null) promo.setValue(request.getValue());
        if (request.getMaxDiscountAmount() != null) promo.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getMinOrderAmount() != null) promo.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getValidFrom() != null) promo.setValidFrom(request.getValidFrom());
        if (request.getValidTo() != null) promo.setValidTo(request.getValidTo());
        if (request.getUsageLimitGlobal() != null) promo.setUsageLimitGlobal(request.getUsageLimitGlobal());
        if (request.getUsageLimitPerUser() != null) promo.setUsageLimitPerUser(request.getUsageLimitPerUser());

        promo = promoCodeRepository.save(promo);
        return PromoCodeResponse.fromEntity(promo);
    }

    // =========================================================
    // READ
    // =========================================================

    /**
     * Gets all promo codes with pagination.
     */
    public Page<PromoCodeResponse> getAllPromos(Pageable pageable, String status) {

        Page<PromoCode> promos;

        if (status != null && !status.isEmpty()) {
            promos = promoCodeRepository.findByStatus(
                    PromoStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            promos = promoCodeRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return promos.map(PromoCodeResponse::fromEntity);
    }

    /**
     * Gets promo code by ID.
     */
    public PromoCodeResponse getPromo(String promoCodeId) {

        PromoCode promo = promoCodeRepository.findByPromoCodeId(promoCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        return PromoCodeResponse.fromEntity(promo);
    }

    // =========================================================
    // APPLY
    // =========================================================

    /**
     * Validates and applies a promo code.
     */
    @Transactional
    public ApplyPromoResponse applyPromo(ApplyPromoRequest request, String userId) {

        log.info("Applying promo code: {} for user: {}", request.getCode(), userId);

        PromoCode promo = promoCodeRepository
                .findByCodeIgnoreCaseAndStatus(request.getCode(), PromoStatus.ACTIVE)
                .orElse(null);

        if (promo == null) {
            return ApplyPromoResponse.builder()
                    .valid(false)
                    .code(request.getCode())
                    .orderTotal(request.getOrderTotal())
                    .errorCode("INVALID_CODE")
                    .message("Invalid or expired promo code")
                    .build();
        }

        String validationError = validatePromo(promo, userId, request);
        if (validationError != null) {
            return ApplyPromoResponse.builder()
                    .valid(false)
                    .code(request.getCode())
                    .orderTotal(request.getOrderTotal())
                    .errorCode("VALIDATION_FAILED")
                    .message(validationError)
                    .build();
        }

        BigDecimal discount = promo.calculateDiscount(request.getOrderTotal());
        BigDecimal finalAmount = request.getOrderTotal().subtract(discount);

        return ApplyPromoResponse.builder()
                .valid(true)
                .promoCodeId(promo.getPromoCodeId())
                .code(promo.getCode())
                .orderTotal(request.getOrderTotal())
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .message("Promo code applied successfully")
                .build();
    }

    // =========================================================
    // REDEEM
    // =========================================================

    /**
     * Records promo redemption (called when order is placed).
     */
    @Transactional
    public PromoRedemption redeemPromo(String promoCodeId,
                                       String userId,
                                       String orderId,
                                       BigDecimal orderAmount,
                                       BigDecimal discountAmount) {

        log.info("Redeeming promo {} for order {}", promoCodeId, orderId);

        PromoCode promo = promoCodeRepository.findByPromoCodeId(promoCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        PromoRedemption redemption = PromoRedemption.builder()
                .redemptionId(UUID.randomUUID().toString())
                .promoCodeId(promoCodeId)
                .promoCode(promo.getCode())
                .userId(userId)
                .orderId(orderId)
                .orderAmount(orderAmount)
                .discountAmount(discountAmount)
                .finalAmount(orderAmount.subtract(discountAmount))
                .build();

        redemption = promoRedemptionRepository.save(redemption);

        // increment usage
        promo.setUsedCount(promo.getUsedCount() + 1);
        promoCodeRepository.save(promo);

        return redemption;
    }

    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Deactivates a promo code.
     */
    @Transactional
    public void deactivatePromo(String promoCodeId) {

        PromoCode promo = promoCodeRepository.findByPromoCodeId(promoCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        promo.setStatus(PromoStatus.INACTIVE);
        promoCodeRepository.save(promo);
    }

    /**
     * Expires old promo codes (scheduled job).
     */
    @Transactional
    public void expireOldPromos() {

        log.info("Running promo expiry job");

        promoCodeRepository.findExpiredPromos(Instant.now())
                .forEach(promo -> {
                    promo.setStatus(PromoStatus.EXPIRED);
                    promoCodeRepository.save(promo);
                    log.info("Expired promo: {}", promo.getCode());
                });
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private String validatePromo(PromoCode promo,
                                 String userId,
                                 ApplyPromoRequest request) {

        Instant now = Instant.now();

        // validity period
        if (promo.getValidFrom() != null && now.isBefore(promo.getValidFrom())) {
            return "Promo code is not yet active";
        }
        if (promo.getValidTo() != null && now.isAfter(promo.getValidTo())) {
            return "Promo code has expired";
        }

        // global usage limit
        if (promo.getUsageLimitGlobal() != null &&
                promo.getUsedCount() >= promo.getUsageLimitGlobal()) {
            return "Promo code usage limit reached";
        }

        // per-user usage limit
        long userUsage =
                promoRedemptionRepository.countByUserIdAndPromoCodeId(
                        userId, promo.getPromoCodeId());

        if (promo.getUsageLimitPerUser() != null &&
                userUsage >= promo.getUsageLimitPerUser()) {
            return "You have already used this promo code";
        }

        // minimum order amount
        if (promo.getMinOrderAmount() != null &&
                request.getOrderTotal()
                        .compareTo(promo.getMinOrderAmount()) < 0) {
            return "Minimum order amount is ₹" + promo.getMinOrderAmount();
        }

        // first order only
        if (Boolean.TRUE.equals(promo.getFirstOrderOnly())) {
            long userOrders = orderRepository.countByUserId(userId);
            if (userOrders > 0) {
                return "This promo is valid for first order only";
            }
        }

        // specific vendors
        if (promo.getApplicableTo() == ApplicableTo.SPECIFIC_VENDORS &&
                promo.getApplicableVendorIds() != null &&
                !promo.getApplicableVendorIds().isEmpty()) {

            if (request.getVendorId() == null ||
                    !promo.getApplicableVendorIds()
                            .contains(request.getVendorId())) {
                return "Promo code is not valid for this vendor";
            }
        }

        // specific users
        if (promo.getApplicableTo() == ApplicableTo.SPECIFIC_USERS &&
                promo.getApplicableUserIds() != null &&
                !promo.getApplicableUserIds().isEmpty()) {

            if (!promo.getApplicableUserIds().contains(userId)) {
                return "Promo code is not valid for your account";
            }
        }

        return null; // valid
    }
}
