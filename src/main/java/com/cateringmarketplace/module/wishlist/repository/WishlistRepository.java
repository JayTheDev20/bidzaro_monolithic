package com.cateringmarketplace.module.wishlist.repository;

import com.cateringmarketplace.module.wishlist.model.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for WishlistItem entity operations.
 */
@Repository
public interface WishlistRepository extends MongoRepository<WishlistItem, String> {

    Optional<WishlistItem> findByWishlistItemId(String wishlistItemId);

    List<WishlistItem> findByUserIdOrderByAddedAtDesc(String userId);

    Page<WishlistItem> findByUserId(String userId, Pageable pageable);

    Optional<WishlistItem> findByUserIdAndVendorItemId(String userId, String vendorItemId);

    boolean existsByUserIdAndVendorItemId(String userId, String vendorItemId);

    long countByUserId(String userId);

    void deleteByWishlistItemId(String wishlistItemId);

    void deleteByUserId(String userId);
}

