package com.cateringmarketplace.module.user.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.dto.response.UserResponse;
import com.cateringmarketplace.module.auth.model.NotificationPreferences;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.user.dto.request.AddressRequest;
import com.cateringmarketplace.module.user.dto.request.UpdateProfileRequest;
import com.cateringmarketplace.module.user.dto.response.AddressResponse;
import com.cateringmarketplace.module.user.service.UserService;
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
 * Controller for user profile and address operations.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User profile and address management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // ==================== ADMIN: USER MANAGEMENT ENDPOINTS ====================

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns list of all users (Admin only). Filter by role using ?role=VENDOR")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponse> users = userService.getAllUsers(pageable, role);

        return ResponseEntity.ok(ApiResponse.success(
                users.getContent(),
                "Users retrieved",
                PageInfo.from(users)
        ));
    }

    // ==================== PROFILE ENDPOINTS ====================

    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Returns current user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.getProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Updates user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Profile update request from user: {}", userDetails.getUserId());
        UserResponse response = userService.updateProfile(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated"));
    }

    @PatchMapping("/profile-picture")
    @Operation(summary = "Update profile picture", description = "Updates user's profile picture")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            @RequestParam String imageUrl,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.updateProfilePicture(imageUrl, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Profile picture updated"));
    }

    @GetMapping("/notification-preferences")
    @Operation(summary = "Get notification preferences", description = "Returns user's notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferences>> getNotificationPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationPreferences preferences = userService.getNotificationPreferences(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @PutMapping("/notification-preferences")
    @Operation(summary = "Update notification preferences", description = "Updates notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferences>> updateNotificationPreferences(
            @Valid @RequestBody NotificationPreferences preferences,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationPreferences updated = userService.updateNotificationPreferences(preferences, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(updated, "Preferences updated"));
    }

    // ==================== ADDRESS ENDPOINTS ====================

    @GetMapping("/addresses")
    @Operation(summary = "Get addresses", description = "Returns all addresses for current user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AddressResponse> addresses = userService.getAddresses(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/addresses/{addressId}")
    @Operation(summary = "Get address", description = "Returns address by ID")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(
            @PathVariable String addressId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressResponse response = userService.getAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add address", description = "Adds a new address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Adding address for user: {}", userDetails.getUserId());
        AddressResponse response = userService.addAddress(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Address added"));
    }

    @PutMapping("/addresses/{addressId}")
    @Operation(summary = "Update address", description = "Updates an address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable String addressId,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressResponse response = userService.updateAddress(addressId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Address updated"));
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Delete address", description = "Deletes an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable String addressId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted"));
    }

    @PatchMapping("/addresses/{addressId}/default")
    @Operation(summary = "Set default address", description = "Sets an address as default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable String addressId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressResponse response = userService.setDefaultAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Default address set"));
    }
}
