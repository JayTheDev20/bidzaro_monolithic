package com.cateringmarketplace.module.cart.repository;

import com.cateringmarketplace.module.cart.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CartItem entity operations.
 */
@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {

    Optional<CartItem> findByCartItemId(String cartItemId);

    List<CartItem> findByUserIdOrderByAddedAtDesc(String userId);

    List<CartItem> findByUserIdAndVendorId(String userId, String vendorId);

    Optional<CartItem> findByUserIdAndVendorItemId(String userId, String vendorItemId);

    boolean existsByUserIdAndVendorItemId(String userId, String vendorItemId);

    long countByUserId(String userId);

    void deleteByCartItemId(String cartItemId);

    void deleteByUserId(String userId);

    @Query("{'user_id': ?0, 'vendor_id': ?1}")
    void deleteByUserIdAndVendorId(String userId, String vendorId);
}

