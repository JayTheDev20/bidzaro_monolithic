package com.cateringmarketplace.module.user.dto.response;

import com.cateringmarketplace.module.user.model.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {

    private String addressId;
    private String userId;
    private String addressType;
    private String label;
    private String fullName;
    private String phone;
    private String streetAddress;
    private String apartment;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String landmark;
    private Boolean isDefault;
    private Instant createdAt;

    public static AddressResponse fromEntity(Address address) {
        if (address == null) return null;

        AddressResponseBuilder builder = AddressResponse.builder()
                .addressId(address.getAddressId())
                .userId(address.getUserId())
                .addressType(address.getAddressType() != null ? address.getAddressType().name() : null)
                .label(address.getLabel())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .streetAddress(address.getStreetAddress())
                .apartment(address.getApartment())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .landmark(address.getLandmark())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt());

        if (address.getGpsCoordinates() != null) {
            builder.longitude(address.getGpsCoordinates().getX());
            builder.latitude(address.getGpsCoordinates().getY());
        }

        return builder.build();
    }
}

