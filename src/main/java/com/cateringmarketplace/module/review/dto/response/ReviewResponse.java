package com.cateringmarketplace.module.review.dto.response;

import com.cateringmarketplace.module.review.model.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {

    private String reviewId;
    private String orderId;
    private String vendorId;
    private String userId;
    private String userName;
    private Integer rating;
    private Integer foodQualityRating;
    private Integer serviceQualityRating;
    private Integer hygieneRating;
    private Integer valueForMoneyRating;
    private Integer punctualityRating;
    private String reviewText;
    private List<String> images;
    private VendorResponseDTO vendorResponse;
    private Integer helpfulCount;
    private String status;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorResponseDTO {
        private String responseText;
        private Instant respondedAt;
    }

    public static ReviewResponse fromEntity(Review review) {
        if (review == null) return null;

        ReviewResponseBuilder builder = ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .orderId(review.getOrderId())
                .vendorId(review.getVendorId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .foodQualityRating(review.getFoodQualityRating())
                .serviceQualityRating(review.getServiceQualityRating())
                .hygieneRating(review.getHygieneRating())
                .valueForMoneyRating(review.getValueForMoneyRating())
                .punctualityRating(review.getPunctualityRating())
                .reviewText(review.getReviewText())
                .images(review.getImages())
                .helpfulCount(review.getHelpfulCount())
                .status(review.getStatus() != null ? review.getStatus().name() : null)
                .createdAt(review.getCreatedAt());

        if (review.getVendorResponse() != null) {
            builder.vendorResponse(VendorResponseDTO.builder()
                    .responseText(review.getVendorResponse().getResponseText())
                    .respondedAt(review.getVendorResponse().getRespondedAt())
                    .build());
        }

        return builder.build();
    }
}

