package com.cateringmarketplace.module.vendor.repository;

import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.model.Vendor.ApprovalStatus;
import com.cateringmarketplace.module.vendor.model.Vendor.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Vendor entity operations.
 */
@Repository
public interface VendorRepository extends MongoRepository<Vendor, String> {

    /**
     * Finds vendor by vendor ID.
     */
    Optional<Vendor> findByVendorId(String vendorId);

    /**
     * Finds vendor by user ID.
     */
    Optional<Vendor> findByUserId(String userId);

    /**
     * Finds vendor by business email.
     */
    Optional<Vendor> findByBusinessEmail(String businessEmail);

    /**
     * Checks if vendor exists with business email.
     */
    boolean existsByBusinessEmail(String businessEmail);

    /**
     * Checks if vendor exists for user.
     */
    boolean existsByUserId(String userId);

    /**
     * Finds vendors by status.
     */
    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);

    /**
     * Finds vendors by approval status.
     */
    Page<Vendor> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);

    /**
     * Finds active and approved vendors.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED'}")
    Page<Vendor> findActiveVendors(Pageable pageable);

    /**
     * Finds featured vendors.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'featured': true}")
    List<Vendor> findFeaturedVendors();

    /**
     * Finds vendors near a location.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'business_address.gps_coordinates': " +
           "{'$nearSphere': {'$geometry': {'type': 'Point', 'coordinates': [?0, ?1]}, '$maxDistance': ?2}}}")
    List<Vendor> findNearbyVendors(double longitude, double latitude, double maxDistanceMeters);

    /**
     * Finds vendors by city.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', " +
           "'$or': [{'business_address.city': ?0}, {'service_areas.city': ?0}]}")
    Page<Vendor> findByCity(String city, Pageable pageable);

    /**
     * Finds vendors by cuisine.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'cuisines_offered': ?0}")
    Page<Vendor> findByCuisine(String cuisine, Pageable pageable);

    /**
     * Searches vendors by name or description.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', " +
           "'$or': [{'business_name': {'$regex': ?0, '$options': 'i'}}, " +
           "{'description': {'$regex': ?0, '$options': 'i'}}]}")
    Page<Vendor> searchVendors(String searchTerm, Pageable pageable);

    /**
     * Finds vendors by rating threshold.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'ratings.average_rating': {'$gte': ?0}}")
    Page<Vendor> findByMinRating(double minRating, Pageable pageable);

    /**
     * Finds vendors by price range.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', " +
           "'pricing.starting_price_per_plate': {'$gte': ?0, '$lte': ?1}}")
    Page<Vendor> findByPriceRange(double minPrice, double maxPrice, Pageable pageable);

    /**
     * Finds pending approval vendors.
     */
    @Query("{'approval_status': 'PENDING'}")
    Page<Vendor> findPendingApproval(Pageable pageable);

    /**
     * Counts vendors by status.
     */
    long countByStatus(VendorStatus status);

    /**
     * Counts vendors by approval status.
     */
    long countByApprovalStatus(ApprovalStatus approvalStatus);

    /**
     * Finds vendors with verified status.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'verified': true}")
    Page<Vendor> findVerifiedVendors(Pageable pageable);

    /**
     * Finds vendors by multiple cuisines.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', 'cuisines_offered': {'$in': ?0}}")
    Page<Vendor> findByCuisines(List<String> cuisines, Pageable pageable);

    /**
     * Finds vendors that can handle guest count.
     */
    @Query("{'status': 'ACTIVE', 'approval_status': 'APPROVED', " +
           "'capacity.min_guests': {'$lte': ?0}, 'capacity.max_guests': {'$gte': ?0}}")
    Page<Vendor> findByGuestCapacity(int guestCount, Pageable pageable);

    /**
     * Finds vendors by business name containing (case insensitive) and status.
     */
    Page<Vendor> findByBusinessNameContainingIgnoreCaseAndStatus(String businessName, VendorStatus status, Pageable pageable);

    /**
     * Count verified vendors.
     */
    long countByVerifiedTrue();

    /**
     * Count vendors created after a date.
     */
    long countByCreatedAtAfter(java.time.Instant date);
}

