package com.cateringmarketplace.module.cart.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.cart.dto.BatchAddDraftRequest;
import com.cateringmarketplace.module.cart.model.DraftCartItem;
import com.cateringmarketplace.module.cart.service.DraftCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart/draft")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Draft Cart", description = "Operations for pre-vendor selection cart")
@SecurityRequirement(name = "bearerAuth")
public class DraftCartController {

    private final DraftCartService draftCartService;

    @GetMapping
    @Operation(summary = "Get draft cart", description = "Returns user's draft cart items")
    public ResponseEntity<ApiResponse<List<DraftCartItem>>> getDraftCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DraftCartItem> items = draftCartService.getDraftCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping("/items")
    @Operation(summary = "Add to draft cart", description = "Adds a master menu item to the draft cart")
    public ResponseEntity<ApiResponse<DraftCartItem>> addToDraftCart(
            @RequestParam String masterItemId,
            @RequestParam(defaultValue = "1") int quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DraftCartItem item = draftCartService.addToDraftCart(userDetails.getUserId(), masterItemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(item, "Item added to draft cart"));
    }

    @PostMapping("/items/batch")
    @Operation(summary = "Batch add to draft cart", description = "Adds multiple master menu items to the draft cart")
    public ResponseEntity<ApiResponse<List<DraftCartItem>>> batchAddToDraftCart(
            @RequestBody BatchAddDraftRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DraftCartItem> items = draftCartService.batchAddToDraftCart(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(items, "Items added to draft cart"));
    }

    @DeleteMapping("/items/{masterItemId}")
    @Operation(summary = "Remove from draft cart", description = "Removes an item from the draft cart")
    public ResponseEntity<ApiResponse<Void>> removeFromDraftCart(
            @PathVariable String masterItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        draftCartService.removeFromDraftCart(userDetails.getUserId(), masterItemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from draft cart"));
    }

    @DeleteMapping
    @Operation(summary = "Clear draft cart", description = "Clears the entire draft cart")
    public ResponseEntity<ApiResponse<Void>> clearDraftCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        draftCartService.clearDraftCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Draft cart cleared"));
    }
}
