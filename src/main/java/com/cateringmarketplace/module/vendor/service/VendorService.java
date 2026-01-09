package com.cateringmarketplace.module.vendor.service;

import com.cateringmarketplace.common.constant.ErrorMessages;
import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.model.User;
import com.cateringmarketplace.module.auth.model.enums.UserType;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.vendor.dto.request.VendorRegistrationRequest;
import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.model.Vendor.*;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for vendor operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final com.cateringmarketplace.module.notification.service.EmailService emailService;

    /**
     * Registers a new vendor.
     */
    @Transactional
    public VendorResponse registerVendor(VendorRegistrationRequest request, String userId) {
        log.info("Registering new vendor for user: {}", userId);

        // Check if user exists
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // Check if vendor already exists for this user
        if (vendorRepository.existsByUserId(userId)) {
            throw new ConflictException("VENDOR_EXISTS", "Vendor profile already exists for this user");
        }

        // Check if business email is already registered
        if (vendorRepository.existsByBusinessEmail(request.getBusinessEmail())) {
            throw new ConflictException("EMAIL_EXISTS", "Business email is already registered");
        }

        // Create vendor
        Vendor vendor = Vendor.builder()
                .userId(userId)
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .businessPhone(request.getBusinessPhone())
                .businessType(BusinessType.valueOf(request.getBusinessType().toUpperCase()))
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .taxId(request.getTaxId())
                .description(request.getDescription())
                .establishedYear(request.getEstablishedYear())
                .cuisinesOffered(request.getCuisinesOffered())
                .specialties(request.getSpecialties())
                .country(request.getCountry())
                .status(VendorStatus.PENDING_APPROVAL)
                .approvalStatus(ApprovalStatus.PENDING)
                .ratings(VendorRatings.builder()
                        .ratingBreakdown(RatingBreakdown.builder().build())
                        .build())
                .stats(VendorStats.builder().build())
                .build();

        // Map business address
        if (request.getBusinessAddress() != null) {
            BusinessAddress address = BusinessAddress.builder()
                    .streetAddress(request.getBusinessAddress().getStreetAddress())
                    .city(request.getBusinessAddress().getCity())
                    .state(request.getBusinessAddress().getState())
                    .postalCode(request.getBusinessAddress().getPostalCode())
                    .country(request.getBusinessAddress().getCountry())
                    .build();

            if (request.getBusinessAddress().getLatitude() != null &&
                request.getBusinessAddress().getLongitude() != null) {
                address.setGpsCoordinates(new GeoJsonPoint(
                        request.getBusinessAddress().getLongitude(),
                        request.getBusinessAddress().getLatitude()
                ));
            }
            vendor.setBusinessAddress(address);
        }

        // Map owner info
        if (request.getOwnerInfo() != null) {
            vendor.setOwnerInfo(OwnerInfo.builder()
                    .firstName(request.getOwnerInfo().getFirstName())
                    .lastName(request.getOwnerInfo().getLastName())
                    .phone(request.getOwnerInfo().getPhone())
                    .email(request.getOwnerInfo().getEmail())
                    .idProofType(request.getOwnerInfo().getIdProofType())
                    .idProofNumber(request.getOwnerInfo().getIdProofNumber())
                    .build());
        }

        // Map service areas
        if (request.getServiceAreas() != null) {
            vendor.setServiceAreas(request.getServiceAreas().stream()
                    .map(sa -> ServiceArea.builder()
                            .city(sa.getCity())
                            .state(sa.getState())
                            .radiusKm(sa.getRadiusKm())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Map capacity
        if (request.getCapacity() != null) {
            vendor.setCapacity(Capacity.builder()
                    .minGuests(request.getCapacity().getMinGuests())
                    .maxGuests(request.getCapacity().getMaxGuests())
                    .concurrentEvents(request.getCapacity().getConcurrentEvents())
                    .build());
        }

        // Map pricing
        if (request.getPricing() != null) {
            vendor.setPricing(Pricing.builder()
                    .currency(request.getPricing().getCurrency() != null ?
                            request.getPricing().getCurrency() : "INR")
                    .startingPricePerPlate(request.getPricing().getStartingPricePerPlate())
                    .averagePricePerPlate(request.getPricing().getAveragePricePerPlate())
                    .build());
        }

        vendor = vendorRepository.save(vendor);

        // Update user type to VENDOR
        user.setUserType(UserType.VENDOR);
        userRepository.save(user);

        log.info("Vendor registered successfully with ID: {}", vendor.getVendorId());
        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Gets all vendors with pagination and filters.
     */
    public Page<VendorResponse> getAllVendors(Pageable pageable, String status, String city, String cuisine) {
        Page<Vendor> vendors;

        if (status != null) {
            vendors = vendorRepository.findByStatus(VendorStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            vendors = vendorRepository.findByStatus(VendorStatus.ACTIVE, pageable);
        }

        return vendors.map(VendorResponse::fromEntity);
    }

    /**
     * Gets vendor by ID.
     */
    public VendorResponse getVendorById(String vendorId) {
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Gets vendor by user ID.
     */
    public VendorResponse getVendorByUserId(String userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found"));
        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Updates vendor profile.
     */
    @Transactional
    public VendorResponse updateVendor(String vendorId, VendorRegistrationRequest request, String userId) {
        log.info("Updating vendor: {} by user: {}", vendorId, userId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // Check ownership
        if (!vendor.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You don't have permission to update this vendor");
        }

        // Update fields
        if (request.getBusinessName() != null) {
            vendor.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessPhone() != null) {
            vendor.setBusinessPhone(request.getBusinessPhone());
        }
        if (request.getDescription() != null) {
            vendor.setDescription(request.getDescription());
        }
        if (request.getCuisinesOffered() != null) {
            vendor.setCuisinesOffered(request.getCuisinesOffered());
        }
        if (request.getSpecialties() != null) {
            vendor.setSpecialties(request.getSpecialties());
        }

        vendor = vendorRepository.save(vendor);
        log.info("Vendor updated successfully: {}", vendorId);

        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Approves a vendor (Admin only).
     */
    @Transactional
    public VendorResponse approveVendor(String vendorId, String adminUserId) {
        log.info("Approving vendor: {} by admin: {}", vendorId, adminUserId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (vendor.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new BadRequestException("ALREADY_APPROVED", "Vendor is already approved");
        }

        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setApprovalStatus(ApprovalStatus.APPROVED);
        vendor.setApprovalDate(Instant.now());
        vendor.setVerified(true);

        vendor = vendorRepository.save(vendor);
        log.info("Vendor approved successfully: {}", vendorId);

        // Send approval email to vendor
        try {
            User vendorUser = userRepository.findByUserId(vendor.getUserId()).orElse(null);
            if (vendorUser != null) {
                emailService.sendVendorApprovalEmail(vendorUser.getEmail(), vendor.getBusinessName());
            }
        } catch (Exception e) {
            log.error("Failed to send vendor approval email: {}", e.getMessage(), e);
        }

        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Rejects a vendor (Admin only).
     */
    @Transactional
    public VendorResponse rejectVendor(String vendorId, String reason, String adminUserId) {
        log.info("Rejecting vendor: {} by admin: {}", vendorId, adminUserId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        vendor.setStatus(VendorStatus.REJECTED);
        vendor.setApprovalStatus(ApprovalStatus.REJECTED);
        vendor.setRejectionReason(reason);

        vendor = vendorRepository.save(vendor);
        log.info("Vendor rejected: {}", vendorId);

        // Send rejection email to vendor
        try {
            User vendorUser = userRepository.findByUserId(vendor.getUserId()).orElse(null);
            if (vendorUser != null) {
                emailService.sendVendorRejectionEmail(vendorUser.getEmail(), vendor.getBusinessName(), reason);
            }
        } catch (Exception e) {
            log.error("Failed to send vendor rejection email: {}", e.getMessage(), e);
        }

        return VendorResponse.fromEntity(vendor);
    }

    /**
     * Gets pending vendors for approval.
     */
    public Page<VendorResponse> getPendingVendors(Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        return vendors.map(VendorResponse::fromEntity);
    }

    /**
     * Searches vendors by criteria.
     */
    public Page<VendorResponse> searchVendors(String query, String city, List<String> cuisines,
                                               Double rating, Pageable pageable) {
        // For now, use basic search - can be enhanced with Elasticsearch later
        Page<Vendor> vendors = vendorRepository.findByBusinessNameContainingIgnoreCaseAndStatus(
                query != null ? query : "", VendorStatus.ACTIVE, pageable);
        return vendors.map(VendorResponse::fromEntity);
    }
}

