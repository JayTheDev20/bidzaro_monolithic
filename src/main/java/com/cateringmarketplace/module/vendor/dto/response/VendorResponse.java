package com.cateringmarketplace.module.vendor.dto.response;

import com.cateringmarketplace.module.vendor.model.Vendor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for vendor information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorResponse {

    private String vendorId;
    private String userId;
    private String registeredEmail;
    private String registeredPhone;
    private Boolean registeredEmailVerified;
    private Boolean registeredPhoneVerified;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private Boolean businessEmailVerified;
    private Boolean businessPhoneVerified;
    private String businessType;
    private String businessRegistrationNumber;
    private String taxId;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private Integer establishedYear;
    private List<String> cuisinesOffered;
    private List<String> specialties;
    private BusinessAddressResponse businessAddress;
    private OwnerInfoResponse ownerInfo;
    private List<ServiceAreaResponse> serviceAreas;
    private CapacityResponse capacity;
    private PricingResponse pricing;
    private RatingsResponse ratings;
    private StatsResponse stats;
    private String status;
    private String approvalStatus;
    private Boolean verified;
    private Boolean featured;
    private Instant createdAt;
    private List<VendorDocumentResponse> documents;
    private String country;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessAddressResponse {
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
    public static class OwnerInfoResponse {
        private String firstName;
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
    public static class ServiceAreaResponse {
        private String city;
        private String state;
        private Integer radiusKm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapacityResponse {
        private Integer minGuests;
        private Integer maxGuests;
        private Integer concurrentEvents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingResponse {
        private String currency;
        private BigDecimal startingPricePerPlate;
        private BigDecimal averagePricePerPlate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingsResponse {
        private BigDecimal averageRating;
        private Integer totalReviews;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsResponse {
        private Integer totalOrders;
        private Integer completedOrders;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDocumentResponse {
        private String documentId;
        private String documentType;
        private String documentName;
        private String documentUrl;
        private String documentNumber;
        private Instant issueDate;
        private Instant expiryDate;
        private String verificationStatus;
        private Instant uploadedAt;
    }

    /**
     * Creates a VendorResponse from a Vendor entity.
     */
    public static VendorResponse fromEntity(Vendor vendor) {
        if (vendor == null) return null;

        VendorResponseBuilder builder = VendorResponse.builder()
                .vendorId(vendor.getVendorId())
                .userId(vendor.getUserId())
                .registeredEmail(vendor.getRegisteredEmail())
                .registeredPhone(vendor.getRegisteredPhone())
                .businessName(vendor.getBusinessName())
                .businessEmail(vendor.getBusinessEmail())
                .businessPhone(vendor.getBusinessPhone())
                .businessEmailVerified(vendor.getBusinessEmailVerified())
                .businessPhoneVerified(vendor.getBusinessPhoneVerified())
                .businessType(vendor.getBusinessType() != null ? vendor.getBusinessType().name() : null)
                .businessRegistrationNumber(vendor.getBusinessRegistrationNumber())
                .taxId(vendor.getTaxId())
                .logoUrl(vendor.getLogoUrl())
                .bannerUrl(vendor.getBannerUrl())
                .description(vendor.getDescription())
                .establishedYear(vendor.getEstablishedYear())
                .cuisinesOffered(vendor.getCuisinesOffered())
                .specialties(vendor.getSpecialties())
                .status(vendor.getStatus() != null ? vendor.getStatus().name() : null)
                .approvalStatus(vendor.getApprovalStatus() != null ? vendor.getApprovalStatus().name() : null)
                .verified(vendor.getVerified())
                .featured(vendor.getFeatured())
                .createdAt(vendor.getCreatedAt())
                .country(vendor.getCountry());

        // Map business address
        if (vendor.getBusinessAddress() != null) {
            builder.businessAddress(BusinessAddressResponse.builder()
                    .streetAddress(vendor.getBusinessAddress().getStreetAddress())
                    .city(vendor.getBusinessAddress().getCity())
                    .state(vendor.getBusinessAddress().getState())
                    .postalCode(vendor.getBusinessAddress().getPostalCode())
                    .country(vendor.getBusinessAddress().getCountry())
                    .build());
        }

        // Map owner info
        if (vendor.getOwnerInfo() != null) {
            builder.ownerInfo(OwnerInfoResponse.builder()
                    .firstName(vendor.getOwnerInfo().getFirstName())
                    .lastName(vendor.getOwnerInfo().getLastName())
                    .phone(vendor.getOwnerInfo().getPhone())
                    .email(vendor.getOwnerInfo().getEmail())
                    .idProofType(vendor.getOwnerInfo().getIdProofType())
                    .idProofNumber(vendor.getOwnerInfo().getIdProofNumber())
                    .build());
        }

        // Map service areas
        if (vendor.getServiceAreas() != null) {
            builder.serviceAreas(vendor.getServiceAreas().stream()
                    .map(sa -> ServiceAreaResponse.builder()
                            .city(sa.getCity())
                            .state(sa.getState())
                            .radiusKm(sa.getRadiusKm())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Map capacity
        if (vendor.getCapacity() != null) {
            builder.capacity(CapacityResponse.builder()
                    .minGuests(vendor.getCapacity().getMinGuests())
                    .maxGuests(vendor.getCapacity().getMaxGuests())
                    .concurrentEvents(vendor.getCapacity().getConcurrentEvents())
                    .build());
        }

        // Map pricing
        if (vendor.getPricing() != null) {
            builder.pricing(PricingResponse.builder()
                    .currency(vendor.getPricing().getCurrency())
                    .startingPricePerPlate(vendor.getPricing().getStartingPricePerPlate())
                    .averagePricePerPlate(vendor.getPricing().getAveragePricePerPlate())
                    .build());
        }

        // Map ratings
        if (vendor.getRatings() != null) {
            builder.ratings(RatingsResponse.builder()
                    .averageRating(vendor.getRatings().getAverageRating())
                    .totalReviews(vendor.getRatings().getTotalReviews())
                    .build());
        }

        // Map stats
        if (vendor.getStats() != null) {
            builder.stats(StatsResponse.builder()
                    .totalOrders(vendor.getStats().getTotalOrders())
                    .completedOrders(vendor.getStats().getCompletedOrders())
                    .build());
        }

        // Map documents
        if (vendor.getDocuments() != null) {
            builder.documents(vendor.getDocuments().stream()
                    .map(doc -> VendorDocumentResponse.builder()
                            .documentId(doc.getDocumentId())
                            .documentType(doc.getDocumentType())
                            .documentName(doc.getDocumentName())
                            .documentUrl(doc.getDocumentUrl())
                            .documentNumber(doc.getDocumentNumber())
                            .issueDate(doc.getIssueDate())
                            .expiryDate(doc.getExpiryDate())
                            .verificationStatus(doc.getVerificationStatus() != null ? doc.getVerificationStatus().name() : null)
                            .uploadedAt(doc.getUploadedAt())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
