package com.cateringmarketplace.module.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.review.dto.request.CreateReviewRequest;
import com.cateringmarketplace.module.review.dto.response.ReviewResponse;
import com.cateringmarketplace.module.review.model.Review;
import com.cateringmarketplace.module.review.model.Review.ReviewStatus;
import com.cateringmarketplace.module.review.repository.ReviewRepository;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;

/**
 * Service class for review operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    /**
     * Creates a new review.
     */
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String userId) {

        log.info("Creating review for order: {} by user: {}", request.getOrderId(), userId);

        // Get order
        Order order = orderRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Validate user is the order owner
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot review this order");
        }

        // Check if order is completed
        if (order.getStatus() != Order.OrderStatus.COMPLETED &&
                order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("ORDER_NOT_COMPLETED", "Cannot review an incomplete order");
        }

        // Check if review already exists
        if (reviewRepository.existsByOrderIdAndUserId(request.getOrderId(), userId)) {
            throw new BadRequestException("REVIEW_EXISTS", "You have already reviewed this order");
        }

        // Get vendor ID from order
        String vendorId = order.getVendorOrders().isEmpty()
                ? null
                : order.getVendorOrders().get(0).getVendorId();

        if (vendorId == null) {
            throw new BadRequestException("NO_VENDOR", "No vendor found for this order");
        }

        // Create review
        Review review = Review.builder()
                .reviewId(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .vendorId(vendorId)
                .userId(userId)
                .rating(request.getRating())
                .foodQualityRating(request.getFoodQualityRating())
                .serviceQualityRating(request.getServiceQualityRating())
                .hygieneRating(request.getHygieneRating())
                .valueForMoneyRating(request.getValueForMoneyRating())
                .punctualityRating(request.getPunctualityRating())
                .reviewText(request.getReviewText())
                .images(request.getImages())
                .status(ReviewStatus.APPROVED) // Auto-approve for now
                .build();

        review = reviewRepository.save(review);
        log.info("Review created: {}", review.getReviewId());

        // Update vendor ratings
        updateVendorRatings(vendorId);

        return ReviewResponse.fromEntity(review);
    }

    // =========================================================
    // READ
    // =========================================================

    /**
     * Gets review by ID.
     */
    public ReviewResponse getReview(String reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return ReviewResponse.fromEntity(review);
    }

    /**
     * Gets reviews for a vendor.
     */
    public Page<ReviewResponse> getVendorReviews(String vendorId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findApprovedReviewsByVendorId(vendorId, pageable);
        return reviews.map(ReviewResponse::fromEntity);
    }

    /**
     * Gets reviews by a user.
     */
    public Page<ReviewResponse> getUserReviews(String userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);
        return reviews.map(ReviewResponse::fromEntity);
    }

    // =========================================================
    // VENDOR RESPONSE
    // =========================================================

    /**
     * Adds vendor response to review.
     */
    @Transactional
    public ReviewResponse addVendorResponse(String reviewId, String responseText, String vendorId) {

        log.info("Adding vendor response to review: {}", reviewId);

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Validate vendor owns this review
        if (!review.getVendorId().equals(vendorId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot respond to this review");
        }

        if (review.getVendorResponse() != null) {
            throw new BadRequestException("RESPONSE_EXISTS", "Vendor response already exists");
        }

        review.setVendorResponse(
                Review.VendorResponse.builder()
                        .responseText(responseText)
                        .respondedAt(Instant.now())
                        .build()
        );

        review = reviewRepository.save(review);
        log.info("Vendor response added to review: {}", reviewId);

        return ReviewResponse.fromEntity(review);
    }

    // =========================================================
    // HELPFUL / REPORT
    // =========================================================

    /**
     * Marks review as helpful.
     */
    @Transactional
    public ReviewResponse markHelpful(String reviewId) {

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review = reviewRepository.save(review);

        return ReviewResponse.fromEntity(review);
    }

    /**
     * Reports a review.
     */
    @Transactional
    public void reportReview(String reviewId, String reason, String userId) {

        log.info("Review {} reported by user: {}", reviewId, userId);

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setReportedCount(review.getReportedCount() + 1);

        // If reported too many times, hide for review
        if (review.getReportedCount() >= 5) {
            review.setStatus(ReviewStatus.HIDDEN);
        }

        reviewRepository.save(review);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    /**
     * Updates vendor ratings based on approved reviews.
     */
    private void updateVendorRatings(String vendorId) {

        List<Review> reviews = reviewRepository.findRatingsForVendor(vendorId);

        if (reviews.isEmpty()) return;

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Vendor vendor = vendorRepository.findByVendorId(vendorId).orElse(null);

        if (vendor != null && vendor.getRatings() != null) {
            vendor.getRatings().setAverageRating(
                    BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP)
            );
            vendor.getRatings().setTotalReviews(reviews.size());
            vendorRepository.save(vendor);
        }
    }
}
