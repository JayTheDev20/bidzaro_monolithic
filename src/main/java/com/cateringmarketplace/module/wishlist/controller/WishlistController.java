package com.cateringmarketplace.module.wishlist.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.wishlist.model.WishlistItem;
import com.cateringmarketplace.module.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for wishlist operations.
 */
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wishlist", description = "Wishlist management APIs")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get wishlist", description = "Returns user's wishlist items")
    public ResponseEntity<ApiResponse<List<WishlistItem>>> getWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size);
        Page<WishlistItem> items = wishlistService.getWishlistPaged(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                items.getContent(),
                "Wishlist retrieved",
                PageInfo.from(items)
        ));
    }

    @PostMapping("/items")
    @Operation(summary = "Add to wishlist", description = "Adds a master item to the wishlist")
    public ResponseEntity<ApiResponse<WishlistItem>> addToWishlist(
            @RequestParam String masterItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Adding to wishlist: {} for user {}", masterItemId, userDetails.getUserId());
        WishlistItem item = wishlistService.addToWishlist(userDetails.getUserId(), masterItemId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(item, "Item added to wishlist"));
    }

    @DeleteMapping("/items/{wishlistItemId}")
    @Operation(summary = "Remove from wishlist", description = "Removes an item from the wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable String wishlistItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        wishlistService.removeFromWishlist(wishlistItemId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from wishlist"));
    }

    @DeleteMapping
    @Operation(summary = "Clear wishlist", description = "Clears the entire wishlist")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        wishlistService.clearWishlist(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Wishlist cleared"));
    }

    @GetMapping("/check/{masterItemId}")
    @Operation(summary = "Check if in wishlist", description = "Checks if a master item is in the wishlist")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> isInWishlist(
            @PathVariable String masterItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean inWishlist = wishlistService.isInWishlist(userDetails.getUserId(), masterItemId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("inWishlist", inWishlist)));
    }

    @GetMapping("/count")
    @Operation(summary = "Get wishlist count", description = "Returns number of items in wishlist")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getWishlistCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = wishlistService.getWishlistCount(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }
}
