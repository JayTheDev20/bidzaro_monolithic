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
import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.cateringmarketplace.module.menu.repository.VendorMenuItemRepository;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.vendor.dto.request.VendorRegistrationRequest;
import com.cateringmarketplace.module.vendor.dto.request.VendorUpdateRequest;
import com.cateringmarketplace.module.vendor.dto.response.VendorMenuSimpleResponse;
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
import java.util.ArrayList;
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
    private final VendorMenuItemRepository vendorMenuItemRepository;
    private final OrderRepository orderRepository;
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

        String businessEmail = request.getBusinessEmail().toLowerCase().trim();

        // Check if business email is already registered
        if (vendorRepository.existsByBusinessEmail(businessEmail)) {
            throw new ConflictException("EMAIL_EXISTS", "Business email is already registered");
        }

        // Validate country
        try {
            Country.valueOf(request.getCountry().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_COUNTRY", "Country must be either USA or INDIA");
        }

        // Validate country-specific documents
        validateDocumentsForCountry(request.getCountry(), request.getDocuments());

        // Determine currency based on country
        String currency = "USD";
        if (Country.INDIA.name().equalsIgnoreCase(request.getCountry())) {
            currency = "INR";
        }
        
        // Override if provided in request
        if (request.getPricing() != null && request.getPricing().getCurrency() != null) {
            currency = request.getPricing().getCurrency();
        }

        // Create vendor
        Vendor vendor = Vendor.builder()
                .userId(userId)
                .registeredEmail(user.getEmail()) // Populate from User
                .registeredPhone(user.getPhone()) // Populate from User
                .businessName(request.getBusinessName())
                .businessEmail(businessEmail)
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
        Pricing pricing = Pricing.builder()
                .currency(currency)
                .build();
                
        if (request.getPricing() != null) {
            pricing.setStartingPricePerPlate(request.getPricing().getStartingPricePerPlate());
            pricing.setAveragePricePerPlate(request.getPricing().getAveragePricePerPlate());
        }
        vendor.setPricing(pricing);

        // Map documents
        if (request.getDocuments() != null) {
            vendor.setDocuments(request.getDocuments().stream()
                    .map(doc -> VendorDocument.builder()
                            .documentType(doc.getDocumentType())
                            .documentName(doc.getDocumentName())
                            .documentUrl(doc.getDocumentUrl())
                            .documentNumber(doc.getDocumentNumber())
                            .issueDate(doc.getIssueDate())
                            .expiryDate(doc.getExpiryDate())
                            .uploadedAt(Instant.now())
                            .verificationStatus(DocumentVerificationStatus.PENDING)
                            .build())
                    .collect(Collectors.toList()));
        }

        vendor = vendorRepository.save(vendor);

        // Update user type to VENDOR
        user.setUserType(UserType.VENDOR);
        userRepository.save(user);

        log.info("Vendor registered successfully with ID: {}", vendor.getVendorId());
        return toVendorResponse(vendor);
    }

    private void validateDocumentsForCountry(String country, List<VendorRegistrationRequest.VendorDocumentDTO> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new BadRequestException("MISSING_DOCUMENTS", "Documents are required for registration");
        }

        List<String> requiredDocs = new ArrayList<>();
        if (Country.USA.name().equalsIgnoreCase(country)) {
            requiredDocs.add("EIN"); // Employer Identification Number
            requiredDocs.add("BUSINESS_LICENSE");
            requiredDocs.add("INSURANCE");
        } else if (Country.INDIA.name().equalsIgnoreCase(country)) {
            requiredDocs.add("GST"); // Goods and Services Tax
            requiredDocs.add("PAN"); // Permanent Account Number
            requiredDocs.add("FSSAI"); // Food Safety and Standards Authority of India
        } else {
             throw new BadRequestException("INVALID_COUNTRY", "Country must be either USA or INDIA");
        }

        List<String> uploadedDocTypes = documents.stream()
                .map(VendorRegistrationRequest.VendorDocumentDTO::getDocumentType)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        for (String requiredDoc : requiredDocs) {
            if (!uploadedDocTypes.contains(requiredDoc)) {
                throw new BadRequestException("MISSING_DOCUMENT", "Missing required document: " + requiredDoc + " for country: " + country);
            }
        }
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

        return vendors.map(this::toVendorResponse);
    }

    /**
     * Gets vendor by ID.
     */
    public VendorResponse getVendorById(String vendorId) {
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        return toVendorResponse(vendor);
    }

    /**
     * Gets vendor by user ID.
     */
    public VendorResponse getVendorByUserId(String userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found"));
        return toVendorResponse(vendor);
    }

    /**
     * Gets simplified vendor menu.
     */
    public List<VendorMenuSimpleResponse> getVendorMenuSimple(String vendorId) {
        List<VendorMenuItem> items = vendorMenuItemRepository.findAvailableItemsByVendor(vendorId);
        
        return items.stream()
                .map(item -> VendorMenuSimpleResponse.builder()
                        .vendorItemId(item.getVendorItemId())
                        .customName(item.getCustomName())
                        .pricePerPlate(item.getEffectivePrice())
                        .currency(item.getPricing() != null ? item.getPricing().getCurrency() : "USD")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Updates vendor profile.
     */
    @Transactional
    public VendorResponse updateVendor(String vendorId, VendorUpdateRequest request, String userId) {
        log.info("Updating vendor: {} by user: {}", vendorId, userId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // Check ownership
        if (!vendor.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You don't have permission to update this vendor");
        }

        // Update fields
        if (request.getBusinessName() != null) vendor.setBusinessName(request.getBusinessName());
        if (request.getBusinessPhone() != null) vendor.setBusinessPhone(request.getBusinessPhone());
        if (request.getBusinessEmail() != null) vendor.setBusinessEmail(request.getBusinessEmail().toLowerCase().trim());
        if (request.getBusinessType() != null) vendor.setBusinessType(BusinessType.valueOf(request.getBusinessType().toUpperCase()));
        if (request.getBusinessRegistrationNumber() != null) vendor.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        if (request.getTaxId() != null) vendor.setTaxId(request.getTaxId());
        if (request.getDescription() != null) vendor.setDescription(request.getDescription());
        if (request.getEstablishedYear() != null) vendor.setEstablishedYear(request.getEstablishedYear());
        if (request.getCuisinesOffered() != null) vendor.setCuisinesOffered(request.getCuisinesOffered());
        if (request.getSpecialties() != null) vendor.setSpecialties(request.getSpecialties());
        if (request.getCountry() != null) vendor.setCountry(request.getCountry());

        // Update Business Address
        if (request.getBusinessAddress() != null) {
            BusinessAddress address = vendor.getBusinessAddress() != null ? vendor.getBusinessAddress() : new BusinessAddress();
            if (request.getBusinessAddress().getStreetAddress() != null) address.setStreetAddress(request.getBusinessAddress().getStreetAddress());
            if (request.getBusinessAddress().getCity() != null) address.setCity(request.getBusinessAddress().getCity());
            if (request.getBusinessAddress().getState() != null) address.setState(request.getBusinessAddress().getState());
            if (request.getBusinessAddress().getPostalCode() != null) address.setPostalCode(request.getBusinessAddress().getPostalCode());
            if (request.getBusinessAddress().getCountry() != null) address.setCountry(request.getBusinessAddress().getCountry());
            
            if (request.getBusinessAddress().getLatitude() != null && request.getBusinessAddress().getLongitude() != null) {
                address.setGpsCoordinates(new GeoJsonPoint(
                        request.getBusinessAddress().getLongitude(),
                        request.getBusinessAddress().getLatitude()
                ));
            }
            vendor.setBusinessAddress(address);
        }

        // Update Owner Info
        if (request.getOwnerInfo() != null) {
            OwnerInfo owner = vendor.getOwnerInfo() != null ? vendor.getOwnerInfo() : new OwnerInfo();
            if (request.getOwnerInfo().getFirstName() != null) owner.setFirstName(request.getOwnerInfo().getFirstName());
            if (request.getOwnerInfo().getLastName() != null) owner.setLastName(request.getOwnerInfo().getLastName());
            if (request.getOwnerInfo().getPhone() != null) owner.setPhone(request.getOwnerInfo().getPhone());
            if (request.getOwnerInfo().getEmail() != null) owner.setEmail(request.getOwnerInfo().getEmail());
            if (request.getOwnerInfo().getIdProofType() != null) owner.setIdProofType(request.getOwnerInfo().getIdProofType());
            if (request.getOwnerInfo().getIdProofNumber() != null) owner.setIdProofNumber(request.getOwnerInfo().getIdProofNumber());
            vendor.setOwnerInfo(owner);
        }

        // Update Service Areas
        if (request.getServiceAreas() != null) {
            vendor.setServiceAreas(request.getServiceAreas().stream()
                    .map(sa -> ServiceArea.builder()
                            .city(sa.getCity())
                            .state(sa.getState())
                            .radiusKm(sa.getRadiusKm())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Update Capacity
        if (request.getCapacity() != null) {
            Capacity capacity = vendor.getCapacity() != null ? vendor.getCapacity() : new Capacity();
            if (request.getCapacity().getMinGuests() != null) capacity.setMinGuests(request.getCapacity().getMinGuests());
            if (request.getCapacity().getMaxGuests() != null) capacity.setMaxGuests(request.getCapacity().getMaxGuests());
            if (request.getCapacity().getConcurrentEvents() != null) capacity.setConcurrentEvents(request.getCapacity().getConcurrentEvents());
            vendor.setCapacity(capacity);
        }

        // Update Pricing
        if (request.getPricing() != null) {
            Pricing pricing = vendor.getPricing() != null ? vendor.getPricing() : new Pricing();
            if (request.getPricing().getCurrency() != null) pricing.setCurrency(request.getPricing().getCurrency());
            if (request.getPricing().getStartingPricePerPlate() != null) pricing.setStartingPricePerPlate(request.getPricing().getStartingPricePerPlate());
            if (request.getPricing().getAveragePricePerPlate() != null) pricing.setAveragePricePerPlate(request.getPricing().getAveragePricePerPlate());
            vendor.setPricing(pricing);
        }

        // Update Documents (Optional: usually handled via separate upload endpoints, but allowing metadata update here)
        if (request.getDocuments() != null) {
            // This replaces the entire document list. For appending, logic would be different.
            vendor.setDocuments(request.getDocuments().stream()
                    .map(doc -> VendorDocument.builder()
                            .documentType(doc.getDocumentType())
                            .documentName(doc.getDocumentName())
                            .documentUrl(doc.getDocumentUrl())
                            .documentNumber(doc.getDocumentNumber())
                            .issueDate(doc.getIssueDate())
                            .expiryDate(doc.getExpiryDate())
                            .uploadedAt(Instant.now())
                            .verificationStatus(DocumentVerificationStatus.PENDING)
                            .build())
                    .collect(Collectors.toList()));
        }

        vendor = vendorRepository.save(vendor);
        log.info("Vendor updated successfully: {}", vendorId);

        return toVendorResponse(vendor);
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

        return toVendorResponse(vendor);
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

        return toVendorResponse(vendor);
    }

    /**
     * Gets pending vendors for approval.
     */
    public Page<VendorResponse> getPendingVendors(Pageable pageable) {
        Page<Vendor> vendors = vendorRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        return vendors.map(this::toVendorResponse);
    }

    /**
     * Gets all vendors with admin filtering (approval status, vendor status, country, search)
     */
    public Page<VendorResponse> getAllVendorsFiltered(Pageable pageable, String approvalStatus,
                                                      String vendorStatus, String country, String search) {
        // Build dynamic query based on filters
        Page<Vendor> vendors;

        // Simple implementation - filters by approval status first, then falls back to all vendors
        if (approvalStatus != null && !approvalStatus.isEmpty()) {
            try {
                vendors = vendorRepository.findByApprovalStatus(ApprovalStatus.valueOf(approvalStatus.toUpperCase()), pageable);
            } catch (IllegalArgumentException e) {
                vendors = vendorRepository.findAll(pageable);
            }
        } else if (vendorStatus != null && !vendorStatus.isEmpty()) {
            try {
                vendors = vendorRepository.findByStatus(VendorStatus.valueOf(vendorStatus.toUpperCase()), pageable);
            } catch (IllegalArgumentException e) {
                vendors = vendorRepository.findAll(pageable);
            }
        } else {
            // If no filters, return all vendors
            vendors = vendorRepository.findAll(pageable);
        }

        return vendors.map(this::toVendorResponse);
    }

    /**
     * Searches vendors by criteria.
     */
    public Page<VendorResponse> searchVendors(String query, String city, List<String> cuisines,
                                               Double rating, Pageable pageable) {
        // For now, use basic search - can be enhanced with Elasticsearch later
        Page<Vendor> vendors = vendorRepository.findByBusinessNameContainingIgnoreCaseAndStatus(
                query != null ? query : "", VendorStatus.ACTIVE, pageable);
        return vendors.map(this::toVendorResponse);
    }

    /**
     * Suspends an active vendor account (Admin only).
     */
    @Transactional
    public VendorResponse suspendVendor(String vendorId, String reason, String adminUserId) {
        log.info("Suspending vendor: {} by admin: {}", vendorId, adminUserId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (vendor.getStatus() == VendorStatus.SUSPENDED) {
            throw new BadRequestException("ALREADY_SUSPENDED", "Vendor is already suspended");
        }

        vendor.setStatus(VendorStatus.SUSPENDED);
        // Store suspension reason in rejectionReason field if needed
        if (reason != null && !reason.isEmpty()) {
            vendor.setRejectionReason("SUSPENDED: " + reason);
        }

        vendor = vendorRepository.save(vendor);
        log.info("Vendor suspended: {}", vendorId);

        return toVendorResponse(vendor);
    }

    /**
     * Activates a suspended or inactive vendor (Admin only).
     */
    @Transactional
    public VendorResponse activateVendor(String vendorId, String adminUserId) {
        log.info("Activating vendor: {} by admin: {}", vendorId, adminUserId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (vendor.getStatus() == VendorStatus.ACTIVE) {
            throw new BadRequestException("ALREADY_ACTIVE", "Vendor is already active");
        }

        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setRejectionReason(null);

        vendor = vendorRepository.save(vendor);
        log.info("Vendor activated: {}", vendorId);

        return toVendorResponse(vendor);
    }

    /**
     * Unlocks a locked vendor account (Admin only).
     */
    @Transactional
    public VendorResponse unlockVendor(String vendorId, String adminUserId) {
        log.info("Unlocking vendor: {} by admin: {}", vendorId, adminUserId);

        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // Ensure vendor status is ACTIVE and clear any rejection reason
        if (vendor.getStatus() != VendorStatus.ACTIVE) {
            vendor.setStatus(VendorStatus.ACTIVE);
        }
        vendor.setRejectionReason(null);

        vendor = vendorRepository.save(vendor);
        log.info("Vendor unlocked: {}", vendorId);

        return toVendorResponse(vendor);
    }

    /**
     * Helper method to convert Vendor entity to VendorResponse and populate verification status from User.
     */
    private VendorResponse toVendorResponse(Vendor vendor) {
        VendorResponse response = VendorResponse.fromEntity(vendor);

        // Fetch user to get verification status
        userRepository.findByUserId(vendor.getUserId()).ifPresent(user -> {
            response.setRegisteredEmailVerified(user.getEmailVerified());
            response.setRegisteredPhoneVerified(user.getPhoneVerified());
        });

        // Fetch real-time orders count from orders collection
        long ordersCount = orderRepository.countByVendorId(vendor.getVendorId());
        if (response.getStats() == null) {
            response.setStats(VendorResponse.StatsResponse.builder()
                    .ordersCount(ordersCount)
                    .build());
        } else {
            response.getStats().setOrdersCount(ordersCount);
        }

        return response;
    }
}
