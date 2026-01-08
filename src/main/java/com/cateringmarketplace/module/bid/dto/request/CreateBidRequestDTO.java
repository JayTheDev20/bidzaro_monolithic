package com.cateringmarketplace.module.bid.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a bid request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBidRequestDTO {

    private List<String> targetedVendors;

    private BudgetDTO budget;

    private AdditionalRequirementsDTO additionalRequirements;

    private List<MenuItemDTO> menuItems;

    @NotNull(message = "Event details are required")
    private EventDetailsDTO eventDetails;

    // =========================================================
    // Event Details
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetailsDTO {

        @NotBlank(message = "Event type is required")
        private String eventType;

        private String eventName;

        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        private LocalDateTime eventDate;

        private String eventStartTime;
        private String eventEndTime;

        @NotNull(message = "Number of guests is required")
        @Min(value = 10, message = "Minimum 10 guests required")
        @Max(value = 10000, message = "Maximum 10000 guests allowed")
        private Integer numberOfGuests;

        @NotNull(message = "Venue address is required")
        private VenueAddressDTO venueAddress;
    }

    // =========================================================
    // Venue Address
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueAddressDTO {

        @NotBlank(message = "Street address is required")
        private String streetAddress;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Postal code is required")
        private String postalCode;

        private String country;

        private Double latitude;
        private Double longitude;
    }

    // =========================================================
    // Menu Item
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemDTO {

        private String vendorItemId;
        private String masterItemId;
        private String itemName;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        private List<CustomizationDTO> customizations;
    }

    // =========================================================
    // Customization
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationDTO {

        private String optionName;
        private String selectedChoice;
    }

    // =========================================================
    // Additional Requirements
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalRequirementsDTO {

        private Boolean serviceStaffNeeded;
        private Integer numberOfStaff;
        private Boolean decorationNeeded;
        private List<String> liveCounters;
        private String specialInstructions;
    }

    // =========================================================
    // Budget
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetDTO {

        private String currency;

        @DecimalMin(value = "0.0", message = "Budget must be positive")
        private BigDecimal estimatedBudget;

        private String budgetRange;
    }
}
