package com.cateringmarketplace.module.vendor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Vendor entity representing catering service providers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vendors")
public class Vendor {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("vendor_id")
    @Builder.Default
    private String vendorId = UUID.randomUUID().toString();

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed(unique = true)
    @Field("business_email")
    private String businessEmail;

    @Field("business_phone")
    private String businessPhone;

    @Field("business_name")
    private String businessName;

    @Field("business_type")
    private BusinessType businessType;

    @Field("business_registration_number")
    private String businessRegistrationNumber;

    @Field("tax_id")
    private String taxId;

    @Field("logo_url")
    private String logoUrl;

    @Field("banner_url")
    private String bannerUrl;

    private String description;

    @Field("established_year")
    private Integer establishedYear;

    @Field("business_address")
    private BusinessAddress businessAddress;

    @Field("owner_info")
    private OwnerInfo ownerInfo;

    @Field("service_areas")
    private List<ServiceArea> serviceAreas;

    @Field("cuisines_offered")
    private List<String> cuisinesOffered;

    private List<String> specialties;

    private Capacity capacity;

    private Pricing pricing;

    private List<VendorDocument> documents;

    @Field("bank_account")
    private BankAccount bankAccount;

    private VendorRatings ratings;

    private VendorStats stats;

    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING_APPROVAL;

    @Field("approval_status")
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Field("approval_date")
    private Instant approvalDate;

    @Field("rejection_reason")
    private String rejectionReason;

    private String country;

    @Builder.Default
    private Boolean verified = false;

    @Builder.Default
    private Boolean featured = false;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    // Enums
    public enum BusinessType {
        CATERING,
        RESTAURANT,
        CLOUD_KITCHEN,
        HOME_CHEF,
        BAKERY
    }

    public enum VendorStatus {
        PENDING_APPROVAL,
        ACTIVE,
        SUSPENDED,
        REJECTED,
        DELETED
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        UNDER_REVIEW
    }

    public enum Country {
        USA,
        INDIA
    }

    // Embedded Documents
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessAddress {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;

        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        private GeoJsonPoint gpsCoordinates;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfo {
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
    public static class ServiceArea {
        private String city;
        private String state;
        private Integer radiusKm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Capacity {
        private Integer minGuests;
        private Integer maxGuests;
        private Integer concurrentEvents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private String currency;
        private BigDecimal startingPricePerPlate;
        private BigDecimal averagePricePerPlate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDocument {
        @Builder.Default
        private String documentId = UUID.randomUUID().toString();
        private String documentType;
        private String documentName;
        private String documentUrl;
        private String documentNumber;
        private Instant issueDate;
        private Instant expiryDate;
        @Builder.Default
        private DocumentVerificationStatus verificationStatus = DocumentVerificationStatus.PENDING;
        private String verifiedBy;
        private Instant verifiedAt;
        private Instant uploadedAt;
    }

    public enum DocumentVerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccount {
        private String accountHolderName;
        private String bankName;
        private String accountNumberEncrypted;
        private String ifscCode;
        private String accountType;
        private String branch;
        @Builder.Default
        private String verificationStatus = "PENDING";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorRatings {
        @Builder.Default
        private BigDecimal averageRating = BigDecimal.ZERO;
        @Builder.Default
        private Integer totalReviews = 0;
        private RatingBreakdown ratingBreakdown;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingBreakdown {
        @Builder.Default
        private Integer fiveStar = 0;
        @Builder.Default
        private Integer fourStar = 0;
        @Builder.Default
        private Integer threeStar = 0;
        @Builder.Default
        private Integer twoStar = 0;
        @Builder.Default
        private Integer oneStar = 0;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorStats {
        @Builder.Default
        private Integer totalOrders = 0;
        @Builder.Default
        private Integer completedOrders = 0;
        @Builder.Default
        private Integer cancelledOrders = 0;
        private BigDecimal responseTimeHours;
    }

    // Helper methods
    public boolean isActive() {
        return status == VendorStatus.ACTIVE && approvalStatus == ApprovalStatus.APPROVED;
    }

    public String getFullOwnerName() {
        if (ownerInfo == null) return null;
        return ownerInfo.getFirstName() + " " + ownerInfo.getLastName();
    }
}
