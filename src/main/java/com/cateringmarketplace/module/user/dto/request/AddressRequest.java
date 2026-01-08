package com.cateringmarketplace.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    // Address type: HOME, OFFICE, EVENT_VENUE, OTHER
    private String addressType;

    private String label;

    // =========================================================
    // CONTACT
    // =========================================================

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    private String phone;

    // =========================================================
    // ADDRESS DETAILS
    // =========================================================

    @NotBlank(message = "Street address is required")
    @Size(max = 200)
    private String streetAddress;

    @Size(max = 100)
    private String apartment;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    private String postalCode;

    private String country;

    @Size(max = 200)
    private String landmark;

    private Double latitude;
    private Double longitude;

    // =========================================================
    // FLAGS
    // =========================================================

    private Boolean isDefault;
}
