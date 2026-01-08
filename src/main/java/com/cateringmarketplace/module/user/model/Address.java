package com.cateringmarketplace.module.user.model;

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

import java.time.Instant;

/**
 * Address entity for user addresses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "addresses")
public class Address {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("address_id")
    private String addressId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("address_type")
    private AddressType addressType;

    private String label;

    @Field("full_name")
    private String fullName;

    private String phone;

    @Field("street_address")
    private String streetAddress;

    private String apartment;

    private String city;

    private String state;

    @Field("postal_code")
    private String postalCode;

    private String country;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("gps_coordinates")
    private GeoJsonPoint gpsCoordinates;

    private String landmark;

    @Field("is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    public enum AddressType {
        HOME,
        OFFICE,
        EVENT_VENUE,
        OTHER
    }

    /**
     * Gets formatted full address.
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (streetAddress != null) sb.append(streetAddress);
        if (apartment != null) sb.append(", ").append(apartment);
        if (landmark != null) sb.append(", Near ").append(landmark);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (postalCode != null) sb.append(" - ").append(postalCode);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
}

