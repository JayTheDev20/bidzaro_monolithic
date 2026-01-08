package com.cateringmarketplace.module.vendor.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.vendor.dto.request.VendorRegistrationRequest;
import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for vendor operations.
 */
@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendors", description = "Vendor management APIs")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @Operation(summary = "Register as vendor", description = "Creates a new vendor profile for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<VendorResponse>> registerVendor(
            @Valid @RequestBody VendorRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Vendor registration request from user: {}", userDetails.getUserId());
        VendorResponse response = vendorService.registerVendor(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Vendor registered successfully. Pending approval."));
    }

    @GetMapping
    @Operation(summary = "Get all vendors", description = "Returns a paginated list of active vendors")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisine,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VendorResponse> vendors = vendorService.getAllVendors(pageable, status, city, cuisine);

        return ResponseEntity.ok(ApiResponse.success(
                vendors.getContent(),
                "Vendors retrieved successfully",
                PageInfo.from(vendors)
        ));
    }

    @GetMapping("/{vendorId}")
    @Operation(summary = "Get vendor by ID", description = "Returns vendor details by vendor ID")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(
            @PathVariable String vendorId) {
        VendorResponse response = vendorService.getVendorById(vendorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my vendor profile", description = "Returns the vendor profile of the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<VendorResponse>> getMyVendorProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        VendorResponse response = vendorService.getVendorByUserId(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{vendorId}")
    @Operation(summary = "Update vendor profile", description = "Updates the vendor profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable String vendorId,
            @Valid @RequestBody VendorRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Vendor update request: {} by user: {}", vendorId, userDetails.getUserId());
        VendorResponse response = vendorService.updateVendor(vendorId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor updated successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search vendors", description = "Search vendors by name, city, cuisine, etc.")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> searchVendors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) List<String> cuisines,
            @RequestParam(required = false) Double rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorResponse> vendors = vendorService.searchVendors(query, city, cuisines, rating, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                vendors.getContent(),
                "Search results",
                PageInfo.from(vendors)
        ));
    }

    // ==================== ADMIN ENDPOINTS ====================

    @GetMapping("/admin/pending")
    @Operation(summary = "Get pending vendors", description = "Returns vendors pending approval (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getPendingVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<VendorResponse> vendors = vendorService.getPendingVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                vendors.getContent(),
                "Pending vendors retrieved",
                PageInfo.from(vendors)
        ));
    }

    @PostMapping("/{vendorId}/approve")
    @Operation(summary = "Approve vendor", description = "Approves a vendor registration (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(
            @PathVariable String vendorId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Approving vendor: {} by admin: {}", vendorId, userDetails.getUserId());
        VendorResponse response = vendorService.approveVendor(vendorId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor approved successfully"));
    }

    @PostMapping("/{vendorId}/reject")
    @Operation(summary = "Reject vendor", description = "Rejects a vendor registration (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(
            @PathVariable String vendorId,
            @RequestParam String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Rejecting vendor: {} by admin: {}", vendorId, userDetails.getUserId());
        VendorResponse response = vendorService.rejectVendor(vendorId, reason, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Vendor rejected"));
    }
}

