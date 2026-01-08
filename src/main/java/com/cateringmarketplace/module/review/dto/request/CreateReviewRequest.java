package com.cateringmarketplace.module.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer foodQualityRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer serviceQualityRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer hygieneRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer valueForMoneyRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer punctualityRating;

    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String reviewText;

    private List<String> images;
}

