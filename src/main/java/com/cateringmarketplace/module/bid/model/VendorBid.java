package com.cateringmarketplace.module.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Vendor bid entity representing a vendor's quote for a bid request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vendor_bids")
@CompoundIndexes({
    @CompoundIndex(name = "bid_request_vendor_idx", def = "{'bid_request_id': 1, 'vendor_id': 1}", unique = true)
})
public class VendorBid {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("bid_id")
    @Builder.Default
    private String bidId = UUID.randomUUID().toString();

    @Indexed
    @Field("bid_request_id")
    private String bidRequestId;

    @Indexed
    @Field("vendor_id")
    private String vendorId;

    @Field("vendor_name")
    private String vendorName;

    @Field("quoted_price")
    private QuotedPrice quotedPrice;

    @Field("itemized_pricing")
    @Builder.Default
    private List<ItemizedPrice> itemizedPricing = new ArrayList<>();

    @Field("delivery_details")
    private DeliveryDetails deliveryDetails;

    @Field("staff_provided")
    private StaffProvided staffProvided;

    @Field("terms_and_conditions")
    private String termsAndConditions;

    @Field("validity_period_hours")
    @Builder.Default
    private Integer validityPeriodHours = 168;

    @Field("advance_percentage")
    private BigDecimal advancePercentage;

    @Field("required_advance_amount")
    private BigDecimal requiredAdvanceAmount;

    @Field("bid_history")
    @Builder.Default
    private List<BidRevision> bidHistory = new ArrayList<>();

    @Indexed
    @Builder.Default
    private BidStatus status = BidStatus.SUBMITTED;

    @Field("is_lowest")
    @Builder.Default
    private Boolean isLowest = false;

    private Integer rank;

    @Field("submitted_at")
    private Instant submittedAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    // Enums
    public enum BidStatus {
        SUBMITTED,
        REVISED,
        ACCEPTED,
        REJECTED,
        EXPIRED,
        WITHDRAWN
    }

    // Embedded Documents
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotedPrice {
        @Builder.Default
        private String currency = "USD";
        private BigDecimal subtotal;
        private BigDecimal serviceCharge;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemizedPrice {
        private String vendorItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal pricePerPlate;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryDetails {
        private String estimatedSetupTime;
        private String foodReadyTime;
        private String cleanupTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffProvided {
        @Builder.Default
        private Integer chefs = 0;
        @Builder.Default
        private Integer servers = 0;
        @Builder.Default
        private Integer cleaners = 0;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidRevision {
        private Integer version;
        private BigDecimal quotedAmount;
        private Instant submittedAt;
        private String revisionReason;
    }

    // Helper methods
    public boolean isValid() {
        return status == BidStatus.SUBMITTED || status == BidStatus.REVISED;
    }

    /**
     * Checks if bid is active (can be revised or accepted).
     */
    public boolean isActive() {
        return status == BidStatus.SUBMITTED || status == BidStatus.REVISED;
    }

    /**
     * Gets the total bid amount.
     */
    public BigDecimal getTotalAmount() {
        return quotedPrice != null ? quotedPrice.getTotalAmount() : BigDecimal.ZERO;
    }

    public boolean canBeRevised() {
        return (status == BidStatus.SUBMITTED || status == BidStatus.REVISED) &&
               bidHistory.size() < 5;
    }

    public int getRevisionCount() {
        return bidHistory.size();
    }

    public void addRevision(BigDecimal previousAmount, String reason) {
        BidRevision revision = BidRevision.builder()
                .version(bidHistory.size() + 1)
                .quotedAmount(previousAmount)
                .submittedAt(Instant.now())
                .revisionReason(reason)
                .build();
        bidHistory.add(revision);
        status = BidStatus.REVISED;
    }
}
