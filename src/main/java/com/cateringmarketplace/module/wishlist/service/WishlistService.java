package com.cateringmarketplace.module.wishlist.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.menu.model.MenuItem;
import com.cateringmarketplace.module.menu.repository.MenuItemRepository;
import com.cateringmarketplace.module.wishlist.model.WishlistItem;
import com.cateringmarketplace.module.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service class for wishlist operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Gets wishlist items for a user.
     */
    public List<WishlistItem> getWishlist(String userId) {
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(userId);
    }

    /**
     * Gets wishlist items with pagination.
     */
    public Page<WishlistItem> getWishlistPaged(String userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable);
    }

    /**
     * Adds master item to wishlist.
     */
    @Transactional
    public WishlistItem addToWishlist(String userId, String masterItemId) {
        log.info("Adding master item {} to wishlist for user {}", masterItemId, userId);

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndMasterItemId(userId, masterItemId)) {
            throw new ConflictException("ALREADY_IN_WISHLIST", "Item is already in your wishlist");
        }

        MenuItem masterItem = menuItemRepository.findByMasterItemId(masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Master menu item not found"));

        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlistItemId(UUID.randomUUID().toString())
                .userId(userId)
                .masterItemId(masterItemId)
                .itemName(masterItem.getItemName())
                .description(masterItem.getDescription())
                .categoryId(masterItem.getCategoryId())
                .cuisineType(masterItem.getCuisineType())
                .foodType(masterItem.getFoodType() != null ? masterItem.getFoodType().name() : null)
                .imageUrl(masterItem.getPrimaryImageUrl())
                .addedAt(Instant.now())
                .build();

        wishlistItem = wishlistRepository.save(wishlistItem);
        log.info("Added to wishlist: {}", wishlistItem.getWishlistItemId());

        return wishlistItem;
    }

    /**
     * Removes item from wishlist.
     */
    @Transactional
    public void removeFromWishlist(String wishlistItemId, String userId) {
        log.info("Removing wishlist item: {}", wishlistItemId);

        WishlistItem item = wishlistRepository.findByWishlistItemId(wishlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new BadRequestException("FORBIDDEN", "You cannot remove this wishlist item");
        }

        wishlistRepository.delete(item);
    }

    /**
     * Removes item from wishlist by master item ID.
     */
    @Transactional
    public void removeFromWishlistByMasterItem(String masterItemId, String userId) {
        WishlistItem item = wishlistRepository.findByUserIdAndMasterItemId(userId, masterItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistRepository.delete(item);
    }

    /**
     * Clears entire wishlist.
     */
    @Transactional
    public void clearWishlist(String userId) {
        log.info("Clearing wishlist for user: {}", userId);
        wishlistRepository.deleteByUserId(userId);
    }

    /**
     * Checks if item is in wishlist.
     */
    public boolean isInWishlist(String userId, String masterItemId) {
        return wishlistRepository.existsByUserIdAndMasterItemId(userId, masterItemId);
    }

    /**
     * Gets wishlist count.
     */
    public long getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}
