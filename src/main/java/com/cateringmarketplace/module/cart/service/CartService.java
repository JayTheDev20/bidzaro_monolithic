package com.cateringmarketplace.module.cart.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.cart.dto.BatchAddToCartRequest;
import com.cateringmarketplace.module.cart.model.CartItem;
import com.cateringmarketplace.module.cart.repository.CartRepository;
import com.cateringmarketplace.module.cart.repository.DraftCartRepository;
import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.cateringmarketplace.module.menu.repository.VendorMenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for cart operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final DraftCartRepository draftCartRepository;
    private final VendorMenuItemRepository vendorMenuItemRepository;

    /**
     * Gets cart items for a user.
     */
    public List<CartItem> getCart(String userId) {
        return cartRepository.findByUserIdOrderByAddedAtDesc(userId);
    }

    /**
     * Gets cart items grouped by vendor.
     */
    public Map<String, List<CartItem>> getCartGroupedByVendor(String userId) {
        List<CartItem> items = cartRepository.findByUserIdOrderByAddedAtDesc(userId);
        return items.stream().collect(Collectors.groupingBy(CartItem::getVendorId));
    }

    /**
     * Adds item to cart.
     */
    @Transactional
    public CartItem addToCart(String userId, String vendorItemId, int quantity) {
        log.info("Adding item {} to cart for user {}", vendorItemId, userId);

        if (quantity < 1) {
            throw new BadRequestException("INVALID_QUANTITY", "Quantity must be at least 1");
        }

        VendorMenuItem vendorItem = vendorMenuItemRepository.findByVendorItemId(vendorItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (!vendorItem.isAvailable()) {
            throw new BadRequestException("ITEM_UNAVAILABLE", "This item is currently unavailable");
        }

        // Check if item already in cart
        CartItem existingItem = cartRepository.findByUserIdAndVendorItemId(userId, vendorItemId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.updateQuantity(existingItem.getQuantity() + quantity);
            return cartRepository.save(existingItem);
        }

        // Create new cart item
        CartItem cartItem = CartItem.builder()
                .cartItemId(UUID.randomUUID().toString())
                .userId(userId)
                .vendorId(vendorItem.getVendorId())
                .vendorItemId(vendorItemId)
                .itemName(vendorItem.getCustomName())
                .quantity(quantity)
                .pricePerPlate(vendorItem.getEffectivePrice())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        cartItem.calculateTotalPrice();
        cartItem = cartRepository.save(cartItem);

        log.info("Added cart item: {}", cartItem.getCartItemId());
        return cartItem;
    }

    /**
     * Batch adds items to cart for a specific vendor.
     * This is used after the user selects a vendor from the matching screen.
     */
    @Transactional
    public List<CartItem> batchAddToCart(String userId, BatchAddToCartRequest request) {
        log.info("Batch adding {} items to cart for user {} and vendor {}", 
                request.getItems().size(), userId, request.getVendorId());

        List<CartItem> addedItems = new ArrayList<>();

        for (BatchAddToCartRequest.ItemRequest itemRequest : request.getItems()) {
            // Find the specific VendorMenuItem using VendorId + MasterItemId
            VendorMenuItem vendorItem = vendorMenuItemRepository.findByVendorIdAndMasterItemId(
                    request.getVendorId(), itemRequest.getMasterItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Item " + itemRequest.getMasterItemId() + " not found for this vendor"));

            if (!vendorItem.isAvailable()) {
                throw new BadRequestException("ITEM_UNAVAILABLE", 
                        "Item " + vendorItem.getCustomName() + " is currently unavailable from this vendor");
            }

            // Check if item already in cart
            CartItem existingItem = cartRepository.findByUserIdAndVendorItemId(userId, vendorItem.getVendorItemId())
                    .orElse(null);

            if (existingItem != null) {
                existingItem.updateQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
                addedItems.add(cartRepository.save(existingItem));
            } else {
                CartItem cartItem = CartItem.builder()
                        .cartItemId(UUID.randomUUID().toString())
                        .userId(userId)
                        .vendorId(request.getVendorId())
                        .vendorItemId(vendorItem.getVendorItemId())
                        .itemName(vendorItem.getCustomName())
                        .quantity(itemRequest.getQuantity())
                        .pricePerPlate(vendorItem.getEffectivePrice())
                        .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                        .build();

                cartItem.calculateTotalPrice();
                addedItems.add(cartRepository.save(cartItem));
            }

            // Remove from Draft Cart if it exists
            draftCartRepository.deleteByUserIdAndMasterItemId(userId, itemRequest.getMasterItemId());
        }

        return addedItems;
    }

    /**
     * Updates cart item quantity.
     */
    @Transactional
    public CartItem updateCartItem(String cartItemId, int quantity, String userId) {
        log.info("Updating cart item {} quantity to {}", cartItemId, quantity);

        CartItem item = cartRepository.findByCartItemId(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot update this cart item");
        }

        if (quantity < 1) {
            throw new BadRequestException("INVALID_QUANTITY", "Quantity must be at least 1");
        }

        item.updateQuantity(quantity);
        return cartRepository.save(item);
    }

    /**
     * Removes item from cart.
     */
    @Transactional
    public void removeFromCart(String cartItemId, String userId) {
        log.info("Removing cart item: {}", cartItemId);

        CartItem item = cartRepository.findByCartItemId(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot remove this cart item");
        }

        cartRepository.delete(item);
    }

    /**
     * Clears entire cart.
     */
    @Transactional
    public void clearCart(String userId) {
        log.info("Clearing cart for user: {}", userId);
        cartRepository.deleteByUserId(userId);
    }

    /**
     * Gets cart item count.
     */
    public long getCartCount(String userId) {
        return cartRepository.countByUserId(userId);
    }

    /**
     * Calculates cart total.
     */
    public BigDecimal getCartTotal(String userId) {
        List<CartItem> items = cartRepository.findByUserIdOrderByAddedAtDesc(userId);
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
