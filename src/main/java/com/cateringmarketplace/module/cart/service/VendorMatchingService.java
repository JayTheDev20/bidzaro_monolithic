package com.cateringmarketplace.module.cart.service;

import com.cateringmarketplace.module.cart.dto.VendorMatchRequest;
import com.cateringmarketplace.module.cart.dto.VendorMatchResponse;
import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.cateringmarketplace.module.menu.repository.VendorMenuItemRepository;
import com.cateringmarketplace.module.vendor.dto.response.VendorResponse;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorMatchingService {

    private final VendorRepository vendorRepository;
    private final VendorMenuItemRepository vendorMenuItemRepository;

    /**
     * Finds vendors matching the requested items and location.
     */
    public List<VendorMatchResponse> findMatchingVendors(VendorMatchRequest request) {
        log.info("Matching vendors for {} items in {}", request.getMasterItemIds().size(), request.getCity());

        // 1. Find candidate vendors based on location
        List<Vendor> candidateVendors;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            double radius = request.getRadiusKm() != null ? request.getRadiusKm() * 1000 : 50000; // Default 50km
            candidateVendors = vendorRepository.findNearbyVendors(
                    request.getLongitude(), request.getLatitude(), radius);
        } else if (request.getCity() != null) {
            // Fetch a reasonable number of vendors in the city
            candidateVendors = vendorRepository.findByCity(request.getCity(), PageRequest.of(0, 100)).getContent();
        } else {
            return Collections.emptyList();
        }

        if (candidateVendors.isEmpty()) {
            return Collections.emptyList();
        }

        List<VendorMatchResponse> results = new ArrayList<>();

        // 2. For each vendor, check item availability
        for (Vendor vendor : candidateVendors) {
            // Get all active items for this vendor
            List<VendorMenuItem> vendorItems = vendorMenuItemRepository.findAvailableItemsByVendor(vendor.getVendorId());
            
            // Map masterItemId -> VendorMenuItem for quick lookup
            Map<String, VendorMenuItem> vendorItemMap = vendorItems.stream()
                    .collect(Collectors.toMap(VendorMenuItem::getMasterItemId, item -> item));

            List<String> matchedIds = new ArrayList<>();
            List<String> missingIds = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (String masterId : request.getMasterItemIds()) {
                if (vendorItemMap.containsKey(masterId)) {
                    matchedIds.add(masterId);
                    VendorMenuItem item = vendorItemMap.get(masterId);
                    if (item.getPricing() != null && item.getPricing().getPricePerPlate() != null) {
                        totalPrice = totalPrice.add(item.getEffectivePrice());
                    }
                } else {
                    missingIds.add(masterId);
                }
            }

            // Calculate match percentage
            double matchPercentage = 0;
            if (!request.getMasterItemIds().isEmpty()) {
                matchPercentage = (double) matchedIds.size() / request.getMasterItemIds().size() * 100.0;
            }

            // Only include if they have at least one item
            if (matchPercentage > 0) {
                // Create VendorResponse manually or via mapper if available in context
                // Using a simplified mapping here for the DTO
                VendorResponse vendorResponse = VendorResponse.fromEntity(vendor);

                results.add(VendorMatchResponse.builder()
                        .vendor(vendorResponse)
                        .matchPercentage(matchPercentage)
                        .matchedItemCount(matchedIds.size())
                        .totalRequestedItems(request.getMasterItemIds().size())
                        .matchedMasterItemIds(matchedIds)
                        .missingMasterItemIds(missingIds)
                        .estimatedTotalPrice(totalPrice)
                        .build());
            }
        }

        // 3. Sort by Match Percentage (DESC), then by Price (ASC)
        results.sort(Comparator.comparingDouble(VendorMatchResponse::getMatchPercentage).reversed()
                .thenComparing(VendorMatchResponse::getEstimatedTotalPrice));

        return results;
    }
}
