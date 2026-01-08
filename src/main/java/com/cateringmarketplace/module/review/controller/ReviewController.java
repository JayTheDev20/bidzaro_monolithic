package com.cateringmarketplace.module.review.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.review.dto.request.CreateReviewRequest;
import com.cateringmarketplace.module.review.dto.response.ReviewResponse;
import com.cateringmarketplace.module.review.service.ReviewService;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for review operations.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reviews", description = "Review management APIs")
public class ReviewController {

    private final ReviewService reviewService;
    private final VendorService vendorService;

    @PostMapping
    @Operation(summary = "Create review", description = "Creates a review for a completed order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Creating review for order: {} by user: {}", request.getOrderId(), userDetails.getUserId());
        ReviewResponse response = reviewService.createReview(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Review submitted successfully"));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review", description = "Returns review details by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable String reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get vendor reviews", description = "Returns reviews for a vendor")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getVendorReviews(
            @PathVariable String vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReviewResponse> reviews = reviewService.getVendorReviews(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                reviews.getContent(),
                "Reviews retrieved",
                PageInfo.from(reviews)
        ));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my reviews", description = "Returns reviews submitted by current user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReviewResponse> reviews = reviewService.getUserReviews(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                reviews.getContent(),
                "Reviews retrieved",
                PageInfo.from(reviews)
        ));
    }

    @PostMapping("/{reviewId}/vendor-response")
    @Operation(summary = "Add vendor response", description = "Adds vendor response to a review")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<ReviewResponse>> addVendorResponse(
            @PathVariable String reviewId,
            @RequestParam String responseText,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();
        ReviewResponse response = reviewService.addVendorResponse(reviewId, responseText, vendorId);

        return ResponseEntity.ok(ApiResponse.success(response, "Response added"));
    }

    @PostMapping("/{reviewId}/helpful")
    @Operation(summary = "Mark as helpful", description = "Marks a review as helpful")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReviewResponse>> markHelpful(
            @PathVariable String reviewId) {
        ReviewResponse response = reviewService.markHelpful(reviewId);
        return ResponseEntity.ok(ApiResponse.success(response, "Marked as helpful"));
    }

    @PostMapping("/{reviewId}/report")
    @Operation(summary = "Report review", description = "Reports a review for moderation")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> reportReview(
            @PathVariable String reviewId,
            @RequestParam String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.reportReview(reviewId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Review reported"));
    }
}

