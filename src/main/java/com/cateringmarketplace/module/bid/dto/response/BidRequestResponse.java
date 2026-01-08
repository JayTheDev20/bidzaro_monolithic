package com.cateringmarketplace.module.bid.dto.response;

import com.cateringmarketplace.module.bid.model.BidRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for bid request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidRequestResponse {

    private String bidRequestId;
    private String userId;
    private EventDetailsResponse eventDetails;
    private List<MenuItemResponse> menuItems;
    private AdditionalRequirementsResponse additionalRequirements;
    private BudgetResponse budget;
    private List<String> targetedVendors;
    private CompetitivePeriodResponse competitivePeriod;
    private AcceptedBidResponse acceptedBid;
    private String status;
    private Integer totalBidsReceived;
    private BigDecimal lowestBidAmount;
    private Instant createdAt;
    private Instant expiresAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetailsResponse {
        private String eventType;
        private String eventName;
        private LocalDateTime eventDate;
        private String eventStartTime;
        private String eventEndTime;
        private Integer numberOfGuests;
        private VenueAddressResponse venueAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueAddressResponse {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemResponse {
        private String vendorItemId;
        private String masterItemId;
        private String itemName;
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalRequirementsResponse {
        private Boolean serviceStaffNeeded;
        private Integer numberOfStaff;
        private Boolean decorationNeeded;
        private List<String> liveCounters;
        private String specialInstructions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetResponse {
        private String currency;
        private BigDecimal estimatedBudget;
        private String budgetRange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitivePeriodResponse {
        private Instant startTime;
        private Instant endTime;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptedBidResponse {
        private String bidId;
        private String vendorId;
        private Instant acceptedAt;
        private Instant coolingPeriodEnd;
    }

    /**
     * Creates a BidRequestResponse from a BidRequest entity.
     */
    public static BidRequestResponse fromEntity(BidRequest request) {
        if (request == null) return null;

        BidRequestResponseBuilder builder = BidRequestResponse.builder()
                .bidRequestId(request.getBidRequestId())
                .userId(request.getUserId())
                .targetedVendors(request.getTargetedVendors())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .totalBidsReceived(request.getTotalBidsReceived())
                .lowestBidAmount(request.getLowestBidAmount())
                .createdAt(request.getCreatedAt())
                .expiresAt(request.getExpiresAt());

        // Map event details
        if (request.getEventDetails() != null) {
            var ed = request.getEventDetails();
            EventDetailsResponse.EventDetailsResponseBuilder edBuilder = EventDetailsResponse.builder()
                    .eventType(ed.getEventType())
                    .eventName(ed.getEventName())
                    .eventDate(ed.getEventDate())
                    .eventStartTime(ed.getEventStartTime())
                    .eventEndTime(ed.getEventEndTime())
                    .numberOfGuests(ed.getNumberOfGuests());

            if (ed.getVenueAddress() != null) {
                edBuilder.venueAddress(VenueAddressResponse.builder()
                        .streetAddress(ed.getVenueAddress().getStreetAddress())
                        .city(ed.getVenueAddress().getCity())
                        .state(ed.getVenueAddress().getState())
                        .postalCode(ed.getVenueAddress().getPostalCode())
                        .country(ed.getVenueAddress().getCountry())
                        .build());
            }
            builder.eventDetails(edBuilder.build());
        }

        // Map menu items
        if (request.getMenuItems() != null) {
            builder.menuItems(request.getMenuItems().stream()
                    .map(mi -> MenuItemResponse.builder()
                            .vendorItemId(mi.getVendorItemId())
                            .masterItemId(mi.getMasterItemId())
                            .itemName(mi.getItemName())
                            .quantity(mi.getQuantity())
                            .build())
                    .toList());
        }

        // Map additional requirements
        if (request.getAdditionalRequirements() != null) {
            var ar = request.getAdditionalRequirements();
            builder.additionalRequirements(AdditionalRequirementsResponse.builder()
                    .serviceStaffNeeded(ar.getServiceStaffNeeded())
                    .numberOfStaff(ar.getNumberOfStaff())
                    .decorationNeeded(ar.getDecorationNeeded())
                    .liveCounters(ar.getLiveCounters())
                    .specialInstructions(ar.getSpecialInstructions())
                    .build());
        }

        // Map budget
        if (request.getBudget() != null) {
            builder.budget(BudgetResponse.builder()
                    .currency(request.getBudget().getCurrency())
                    .estimatedBudget(request.getBudget().getEstimatedBudget())
                    .budgetRange(request.getBudget().getBudgetRange())
                    .build());
        }

        // Map competitive period
        if (request.getCompetitivePeriod() != null) {
            builder.competitivePeriod(CompetitivePeriodResponse.builder()
                    .startTime(request.getCompetitivePeriod().getStartTime())
                    .endTime(request.getCompetitivePeriod().getEndTime())
                    .status(request.getCompetitivePeriod().getStatus() != null ?
                            request.getCompetitivePeriod().getStatus().name() : null)
                    .build());
        }

        // Map accepted bid
        if (request.getAcceptedBid() != null) {
            builder.acceptedBid(AcceptedBidResponse.builder()
                    .bidId(request.getAcceptedBid().getBidId())
                    .vendorId(request.getAcceptedBid().getVendorId())
                    .acceptedAt(request.getAcceptedBid().getAcceptedAt())
                    .coolingPeriodEnd(request.getAcceptedBid().getCoolingPeriodEnd())
                    .build());
        }

        return builder.build();
    }
}

