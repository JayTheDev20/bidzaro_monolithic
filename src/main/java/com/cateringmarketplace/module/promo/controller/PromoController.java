package com.cateringmarketplace.module.promo.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.promo.dto.request.ApplyPromoRequest;
import com.cateringmarketplace.module.promo.dto.request.CreatePromoRequest;
import com.cateringmarketplace.module.promo.dto.response.ApplyPromoResponse;
import com.cateringmarketplace.module.promo.dto.response.PromoCodeResponse;
import com.cateringmarketplace.module.promo.service.PromoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for promo code operations.
 */
@RestController
@RequestMapping("/promos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promo Codes", description = "Promo code and coupon management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PromoController {

    private final PromoService promoService;

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping
    @Operation(summary = "Create promo code", description = "Creates a new promo code (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> createPromo(
            @Valid @RequestBody CreatePromoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Creating promo code: {} by admin: {}", request.getCode(), userDetails.getUserId());
        PromoCodeResponse response = promoService.createPromo(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Promo code created"));
    }

    @PutMapping("/{promoCodeId}")
    @Operation(summary = "Update promo code", description = "Updates a promo code (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> updatePromo(
            @PathVariable String promoCodeId,
            @Valid @RequestBody CreatePromoRequest request) {
        PromoCodeResponse response = promoService.updatePromo(promoCodeId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Promo code updated"));
    }

    @GetMapping
    @Operation(summary = "Get all promo codes", description = "Returns all promo codes (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PromoCodeResponse>>> getAllPromos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PromoCodeResponse> promos = promoService.getAllPromos(pageable, status);
        return ResponseEntity.ok(ApiResponse.success(
                promos.getContent(),
                "Promo codes retrieved",
                PageInfo.from(promos)
        ));
    }

    @GetMapping("/{promoCodeId}")
    @Operation(summary = "Get promo code", description = "Returns promo code details (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> getPromo(
            @PathVariable String promoCodeId) {
        PromoCodeResponse response = promoService.getPromo(promoCodeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{promoCodeId}")
    @Operation(summary = "Deactivate promo code", description = "Deactivates a promo code (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivatePromo(
            @PathVariable String promoCodeId) {
        promoService.deactivatePromo(promoCodeId);
        return ResponseEntity.ok(ApiResponse.success(null, "Promo code deactivated"));
    }

    // ==================== USER ENDPOINTS ====================

    @PostMapping("/apply")
    @Operation(summary = "Apply promo code", description = "Validates and applies a promo code to an order")
    public ResponseEntity<ApiResponse<ApplyPromoResponse>> applyPromo(
            @Valid @RequestBody ApplyPromoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Applying promo code: {} for user: {}", request.getCode(), userDetails.getUserId());
        ApplyPromoResponse response = promoService.applyPromo(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

