package com.cateringmarketplace.module.review.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Review entity for vendor reviews.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("review_id")
    private String reviewId;

    @Indexed
    @Field("order_id")
    private String orderId;

    @Indexed
    @Field("vendor_id")
    private String vendorId;

    @Indexed
    @Field("user_id")
    private String userId;

    private Integer rating;

    @Field("food_quality_rating")
    private Integer foodQualityRating;

    @Field("service_quality_rating")
    private Integer serviceQualityRating;

    @Field("hygiene_rating")
    private Integer hygieneRating;

    @Field("value_for_money_rating")
    private Integer valueForMoneyRating;

    @Field("punctuality_rating")
    private Integer punctualityRating;

    @Field("review_text")
    private String reviewText;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Field("vendor_response")
    private VendorResponse vendorResponse;

    @Field("helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Field("reported_count")
    @Builder.Default
    private Integer reportedCount = 0;

    private ReviewStatus status;

    @Field("moderation_notes")
    private String moderationNotes;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // Enums
    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED,
        HIDDEN
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorResponse {
        @Field("response_text")
        private String responseText;
        @Field("responded_at")
        private Instant respondedAt;
    }

    /**
     * Calculates average rating from all categories.
     */
    public double getAverageDetailedRating() {
        int count = 0;
        int sum = 0;

        if (foodQualityRating != null) { sum += foodQualityRating; count++; }
        if (serviceQualityRating != null) { sum += serviceQualityRating; count++; }
        if (hygieneRating != null) { sum += hygieneRating; count++; }
        if (valueForMoneyRating != null) { sum += valueForMoneyRating; count++; }
        if (punctualityRating != null) { sum += punctualityRating; count++; }

        return count > 0 ? (double) sum / count : 0;
    }

    /**
     * Checks if review can be edited (within 24 hours).
     */
    public boolean canBeEdited() {
        if (createdAt == null) return false;
        return Instant.now().isBefore(createdAt.plusSeconds(24 * 60 * 60));
    }

    /**
     * Increments helpful count.
     */
    public void incrementHelpfulCount() {
        this.helpfulCount = (this.helpfulCount == null ? 0 : this.helpfulCount) + 1;
    }
}

