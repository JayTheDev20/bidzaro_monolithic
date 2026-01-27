package com.cateringmarketplace.module.cart.dto;

import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorMatchResponse {
    private VendorResponse vendor;
    private double matchPercentage;
    private int matchedItemCount;
    private int totalRequestedItems;
    private List<String> matchedMasterItemIds;
    private List<String> missingMasterItemIds;
    private BigDecimal estimatedTotalPrice; // Sum of prices for matched items
}
