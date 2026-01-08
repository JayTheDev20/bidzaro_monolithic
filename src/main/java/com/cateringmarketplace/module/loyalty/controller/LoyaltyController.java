package com.cateringmarketplace.module.loyalty.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.loyalty.dto.response.LoyaltyBalanceResponse;
import com.cateringmarketplace.module.loyalty.dto.response.LoyaltyTransactionResponse;
import com.cateringmarketplace.module.loyalty.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for loyalty points operations.
 */
@RestController
@RequestMapping("/loyalty")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loyalty", description = "Loyalty points management APIs")
@SecurityRequirement(name = "bearerAuth")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/balance")
    @Operation(summary = "Get loyalty balance", description = "Returns current user's loyalty points balance and tier")
    public ResponseEntity<ApiResponse<LoyaltyBalanceResponse>> getBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LoyaltyBalanceResponse response = loyaltyService.getBalance(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get loyalty transactions", description = "Returns loyalty points transaction history")
    public ResponseEntity<ApiResponse<List<LoyaltyTransactionResponse>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LoyaltyTransactionResponse> transactions = loyaltyService.getTransactions(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(
                transactions.getContent(),
                "Transactions retrieved",
                PageInfo.from(transactions)
        ));
    }

    @GetMapping("/calculate")
    @Operation(summary = "Calculate potential points", description = "Calculates points that would be earned for an order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculatePoints(
            @RequestParam BigDecimal orderAmount,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int points = loyaltyService.calculatePotentialPoints(userDetails.getUserId(), orderAmount);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "orderAmount", orderAmount,
                "potentialPoints", points
        )));
    }
}

