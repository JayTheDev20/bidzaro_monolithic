package com.cateringmarketplace.module.cart.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.cart.dto.BatchAddToCartRequest;
import com.cateringmarketplace.module.cart.model.CartItem;
import com.cateringmarketplace.module.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for cart operations.
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart", description = "Shopping cart APIs")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get cart", description = "Returns user's cart items")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CartItem> items = cartService.getCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/grouped")
    @Operation(summary = "Get cart grouped by vendor", description = "Returns cart items grouped by vendor")
    public ResponseEntity<ApiResponse<Map<String, List<CartItem>>>> getCartGroupedByVendor(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, List<CartItem>> groupedItems = cartService.getCartGroupedByVendor(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(groupedItems));
    }

    @PostMapping("/items")
    @Operation(summary = "Add to cart", description = "Adds an item to the cart")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @RequestParam String vendorItemId,
            @RequestParam(defaultValue = "1") int quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Adding to cart: {} qty {} for user {}", vendorItemId, quantity, userDetails.getUserId());
        CartItem item = cartService.addToCart(userDetails.getUserId(), vendorItemId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(item, "Item added to cart"));
    }

    @PostMapping("/items/batch")
    @Operation(summary = "Batch add to cart", description = "Adds multiple items from a specific vendor to the cart")
    public ResponseEntity<ApiResponse<List<CartItem>>> batchAddToCart(
            @RequestBody BatchAddToCartRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Batch adding to cart for user {}", userDetails.getUserId());
        List<CartItem> items = cartService.batchAddToCart(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(items, "Items added to cart"));
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update cart item", description = "Updates quantity of a cart item")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItem(
            @PathVariable String cartItemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartItem item = cartService.updateCartItem(cartItemId, quantity, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(item, "Cart item updated"));
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove from cart", description = "Removes an item from the cart")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable String cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeFromCart(cartItemId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from cart"));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Clears the entire cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared"));
    }

    @GetMapping("/count")
    @Operation(summary = "Get cart count", description = "Returns number of items in cart")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCartCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = cartService.getCartCount(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @GetMapping("/total")
    @Operation(summary = "Get cart total", description = "Returns total price of items in cart")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getCartTotal(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BigDecimal total = cartService.getCartTotal(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("total", total)));
    }
}
