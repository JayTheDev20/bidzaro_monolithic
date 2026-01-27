package com.cateringmarketplace.module.menu.repository;

import com.cateringmarketplace.module.menu.model.VendorMenuItem;
import com.cateringmarketplace.module.menu.model.VendorMenuItem.VendorItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for VendorMenuItem entity operations.
 */
@Repository
public interface VendorMenuItemRepository extends MongoRepository<VendorMenuItem, String> {

    Optional<VendorMenuItem> findByVendorItemId(String vendorItemId);

    Page<VendorMenuItem> findByVendorIdAndStatus(String vendorId, VendorItemStatus status, Pageable pageable);

    Page<VendorMenuItem> findByVendorId(String vendorId, Pageable pageable);

    Optional<VendorMenuItem> findByVendorIdAndMasterItemId(String vendorId, String masterItemId);

    boolean existsByVendorIdAndMasterItemId(String vendorId, String masterItemId);

    // Updated query to match Java field name (camelCase) which is the default mapping
    @Query("{'vendor_id': ?0, 'status': 'ACTIVE', 'availability.isAvailable': true}")
    List<VendorMenuItem> findAvailableItemsByVendor(String vendorId);

    @Query("{'vendor_id': ?0, 'status': 'ACTIVE'}")
    Page<VendorMenuItem> findActiveItemsByVendor(String vendorId, Pageable pageable);

    long countByVendorIdAndStatus(String vendorId, VendorItemStatus status);

    void deleteByVendorItemId(String vendorItemId);
}
