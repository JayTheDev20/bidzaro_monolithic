package com.cateringmarketplace.module.vendor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorMenuSimpleResponse {
    private String vendorItemId;
    private String customName;
    private BigDecimal pricePerPlate;
    private String currency;
}
