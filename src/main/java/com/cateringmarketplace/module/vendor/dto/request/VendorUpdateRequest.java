package com.cateringmarketplace.module.vendor.dto.request;

import com.cateringmarketplace.module.vendor.dto.request.VendorRegistrationRequest.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Request DTO for vendor profile update.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorUpdateRequest {

    @Size(min = 2, max = 100)
    private String businessName;

    private String businessEmail;
    private String businessPhone;
    private String businessType;
    private String businessRegistrationNumber;
    private String taxId;
    private String description;
    private Integer establishedYear;
    private List<String> cuisinesOffered;
    private List<String> specialties;
    private String country;

    private BusinessAddressDTO businessAddress;
    private OwnerInfoDTO ownerInfo;
    private List<ServiceAreaDTO> serviceAreas;
    private CapacityDTO capacity;
    private PricingDTO pricing;
    private List<VendorDocumentDTO> documents;
}
