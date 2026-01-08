package com.cateringmarketplace.module.referral.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.referral.dto.response.ReferralCodeResponse;
import com.cateringmarketplace.module.referral.dto.response.ReferralStatsResponse;
import com.cateringmarketplace.module.referral.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for referral operations.
 */
@RestController
@RequestMapping("/referral")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Referral", description = "Referral program APIs")
@SecurityRequirement(name = "bearerAuth")
public class ReferralController {

    private final ReferralService referralService;

    @GetMapping("/code")
    @Operation(summary = "Get referral code", description = "Gets or creates referral code for current user")
    public ResponseEntity<ApiResponse<ReferralCodeResponse>> getReferralCode(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReferralCodeResponse response = referralService.getOrCreateReferralCode(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get referral stats", description = "Gets referral statistics for current user")
    public ResponseEntity<ApiResponse<ReferralStatsResponse>> getReferralStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReferralStatsResponse response = referralService.getReferralStats(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Validate referral code", description = "Checks if a referral code is valid")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateCode(
            @PathVariable String code) {
        boolean valid = referralService.isValidReferralCode(code);
        return ResponseEntity.ok(ApiResponse.success(Map.of("valid", valid)));
    }
}

