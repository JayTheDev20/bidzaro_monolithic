package com.cateringmarketplace.module.cart.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.module.cart.dto.VendorMatchRequest;
import com.cateringmarketplace.module.cart.dto.VendorMatchResponse;
import com.cateringmarketplace.module.cart.service.VendorMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart/match")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendor Matching", description = "APIs for matching vendors to cart items")
public class VendorMatchingController {

    private final VendorMatchingService vendorMatchingService;

    @PostMapping
    @Operation(summary = "Find matching vendors", description = "Finds vendors that match the selected master menu items")
    public ResponseEntity<ApiResponse<List<VendorMatchResponse>>> findMatchingVendors(
            @RequestBody VendorMatchRequest request) {
        
        List<VendorMatchResponse> matches = vendorMatchingService.findMatchingVendors(request);
        
        return ResponseEntity.ok(ApiResponse.success(
                matches, 
                "Found " + matches.size() + " matching vendors"
        ));
    }
}
