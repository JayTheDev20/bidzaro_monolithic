package com.cateringmarketplace.module.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.review.model.Review;
import com.cateringmarketplace.module.review.model.Review.ReviewStatus;

/**
 * Repository for Review entity operations.
 */
@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    // =========================================================
    // FIND BY IDS
    // =========================================================

    /**
     * Find by review ID.
     */
    Optional<Review> findByReviewId(String reviewId);

    /**
     * Find reviews by order ID.
     */
    Optional<Review> findByOrderId(String orderId);

    // =========================================================
    // USER / VENDOR
    // =========================================================

    /**
     * Find reviews by user ID.
     */
    Page<Review> findByUserId(String userId, Pageable pageable);

    /**
     * Find reviews by vendor ID and status.
     */
    Page<Review> findByVendorIdAndStatus(String vendorId, ReviewStatus status, Pageable pageable);

    /**
     * Find approved reviews by vendor ID.
     */
    @Query("{'vendor_id': ?0, 'status': 'APPROVED'}")
    Page<Review> findApprovedReviewsByVendorId(String vendorId, Pageable pageable);

    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Find reviews by status.
     */
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    /**
     * Count reviews by vendor.
     */
    long countByVendorIdAndStatus(String vendorId, ReviewStatus status);

    // =========================================================
    // VALIDATION
    // =========================================================

    /**
     * Check if user has reviewed an order.
     */
    boolean existsByOrderIdAndUserId(String orderId, String userId);

    // =========================================================
    // RATINGS
    // =========================================================

    /**
     * Find ratings for vendor (only rating field returned).
     */
    @Query(value = "{'vendor_id': ?0, 'status': 'APPROVED'}", fields = "{'rating': 1}")
    List<Review> findRatingsForVendor(String vendorId);
}
