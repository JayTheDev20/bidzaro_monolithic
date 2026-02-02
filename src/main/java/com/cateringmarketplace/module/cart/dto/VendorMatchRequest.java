package com.cateringmarketplace.module.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorMatchRequest {
    private List<String> masterItemIds;
    private Double latitude;
    private Double longitude;
    private String city;
    private Double radiusKm; // Optional override for search radius
}
