package com.cateringmarketplace.module.vendor.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Request DTO for vendor registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegistrationRequest {

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 100)
    private String businessName;

    @NotBlank(message = "Business email is required")
    @Email(message = "Invalid email format")
    private String businessEmail;

    @NotBlank(message = "Business phone is required")
    private String businessPhone;

    @NotBlank(message = "Business type is required")
    private String businessType;

    private String businessRegistrationNumber;
    private String taxId;
    private String description;
    private Integer establishedYear;
    private List<String> cuisinesOffered;
    private List<String> specialties;

    @NotBlank(message = "Country is required")
    private String country;

    private BusinessAddressDTO businessAddress;
    private OwnerInfoDTO ownerInfo;
    private List<ServiceAreaDTO> serviceAreas;
    private CapacityDTO capacity;
    private PricingDTO pricing;
    private List<VendorDocumentDTO> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessAddressDTO {
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfoDTO {
        @NotBlank(message = "First name is required")
        private String firstName;
        @NotBlank(message = "Last name is required")
        private String lastName;
        private String phone;
        private String email;
        private String idProofType;
        private String idProofNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceAreaDTO {
        private String city;
        private String state;
        private Integer radiusKm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapacityDTO {
        @Min(value = 10, message = "Minimum guests must be at least 10")
        private Integer minGuests;
        @Max(value = 10000, message = "Maximum guests cannot exceed 10000")
        private Integer maxGuests;
        private Integer concurrentEvents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingDTO {
        private String currency;
        @DecimalMin(value = "0.0", message = "Price must be positive")
        private BigDecimal startingPricePerPlate;
        private BigDecimal averagePricePerPlate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDocumentDTO {
        @NotBlank(message = "Document type is required")
        private String documentType;
        @NotBlank(message = "Document name is required")
        private String documentName;
        @NotBlank(message = "Document URL is required")
        private String documentUrl;
        private String documentNumber;
        private Instant issueDate;
        private Instant expiryDate;
    }
}
