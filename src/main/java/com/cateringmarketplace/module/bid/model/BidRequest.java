package com.cateringmarketplace.module.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bid request entity representing a user's request for catering quotes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bid_requests")
public class BidRequest {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("bid_request_id")
    @Builder.Default
    private String bidRequestId = UUID.randomUUID().toString();

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("event_details")
    private EventDetails eventDetails;

    @Field("menu_items")
    @Builder.Default
    private List<RequestedMenuItem> menuItems = new ArrayList<>();

    @Field("additional_requirements")
    private AdditionalRequirements additionalRequirements;

    private Budget budget;

    @Field("targeted_vendors")
    @Builder.Default
    private List<String> targetedVendors = new ArrayList<>();

    @Field("competitive_period")
    private CompetitivePeriod competitivePeriod;

    @Field("accepted_bid")
    private AcceptedBid acceptedBid;

    @Indexed
    @Builder.Default
    private BidRequestStatus status = BidRequestStatus.DRAFT;

    @Field("total_bids_received")
    @Builder.Default
    private Integer totalBidsReceived = 0;

    @Field("lowest_bid_amount")
    private BigDecimal lowestBidAmount;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("expires_at")
    private Instant expiresAt;

    // Enums
    public enum BidRequestStatus {
        DRAFT,
        ACTIVE,
        COMPETITIVE,
        COOLING,
        ACCEPTED,
        EXPIRED,
        CANCELLED
    }

    // Embedded Documents
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetails {
        private String eventType;
        private String eventName;
        @Indexed
        private LocalDateTime eventDate;
        private String eventStartTime;
        private String eventEndTime;
        private Integer numberOfGuests;
        private VenueAddress venueAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueAddress {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private GeoJsonPoint gpsCoordinates;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestedMenuItem {
        private String vendorItemId;
        private String masterItemId;
        private String itemName;
        private Integer quantity;
        private List<MenuCustomization> customizations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuCustomization {
        private String optionName;
        private String selectedChoice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalRequirements {
        @Builder.Default
        private Boolean serviceStaffNeeded = false;
        private Integer numberOfStaff;
        @Builder.Default
        private Boolean decorationNeeded = false;
        private List<String> liveCounters;
        private String specialInstructions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Budget {
        @Builder.Default
        private String currency = "INR";
        private BigDecimal estimatedBudget;
        private String budgetRange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitivePeriod {
        private Instant startTime;
        private Instant endTime;
        @Builder.Default
        private PeriodStatus status = PeriodStatus.ACTIVE;
    }

    public enum PeriodStatus {
        ACTIVE,
        ENDED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptedBid {
        private String bidId;
        private String vendorId;
        private Instant acceptedAt;
        private Instant coolingPeriodEnd;
    }

    // Helper methods
    public boolean isActive() {
        return status == BidRequestStatus.ACTIVE || status == BidRequestStatus.COMPETITIVE;
    }

    public boolean canAcceptBids() {
        return status == BidRequestStatus.ACTIVE || status == BidRequestStatus.COMPETITIVE;
    }

    public boolean isInCoolingPeriod() {
        return status == BidRequestStatus.COOLING &&
               acceptedBid != null &&
               Instant.now().isBefore(acceptedBid.getCoolingPeriodEnd());
    }

    public void incrementBidsReceived() {
        this.totalBidsReceived++;
    }

    public void updateLowestBid(BigDecimal amount) {
        if (this.lowestBidAmount == null || amount.compareTo(this.lowestBidAmount) < 0) {
            this.lowestBidAmount = amount;
        }
    }
}
