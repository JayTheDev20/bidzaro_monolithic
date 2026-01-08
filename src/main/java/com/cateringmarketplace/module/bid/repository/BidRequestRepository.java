package com.cateringmarketplace.module.bid.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.model.BidRequest.BidRequestStatus;

/**
 * Repository for BidRequest entity operations.
 */
@Repository
public interface BidRequestRepository extends MongoRepository<BidRequest, String> {

    // =========================================================
    // FIND BY ID
    // =========================================================

    /**
     * Find by bid request ID.
     */
    Optional<BidRequest> findByBidRequestId(String bidRequestId);

    /**
     * Delete by bid request ID.
     */
    void deleteByBidRequestId(String bidRequestId);

    // =========================================================
    // USER BASED QUERIES
    // =========================================================

    /**
     * Find bid requests by user ID.
     */
    Page<BidRequest> findByUserId(String userId, Pageable pageable);

    /**
     * Find bid requests by user ID and status.
     */
    Page<BidRequest> findByUserIdAndStatus(String userId, BidRequestStatus status, Pageable pageable);

    /**
     * Count bid requests by user ID.
     */
    long countByUserId(String userId);

    // =========================================================
    // STATUS BASED QUERIES
    // =========================================================

    /**
     * Find bid requests by status.
     */
    Page<BidRequest> findByStatus(BidRequestStatus status, Pageable pageable);

    /**
     * Count bid requests by status.
     */
    long countByStatus(BidRequestStatus status);

    // =========================================================
    // ACTIVE / EXPIRED / COOLING
    // =========================================================

    /**
     * Find active bid requests (for vendors to bid on).
     */
    @Query("{'status': {'$in': ['ACTIVE', 'COMPETITIVE']}, 'expires_at': {'$gt': ?0}}")
    Page<BidRequest> findActiveBidRequests(Instant now, Pageable pageable);

    /**
     * Find expired bid requests that need status update.
     */
    @Query("{'status': {'$in': ['ACTIVE', 'COMPETITIVE']}, 'expires_at': {'$lt': ?0}}")
    List<BidRequest> findExpiredBidRequests(Instant now);

    /**
     * Find bid requests in cooling period that have ended.
     */
    @Query("{'status': 'COOLING', 'accepted_bid.cooling_period_end': {'$lt': ?0}}")
    List<BidRequest> findCoolingPeriodEndedRequests(Instant now);

    // =========================================================
    // VENDOR TARGETING
    // =========================================================

    /**
     * Find bid requests targeted to a specific vendor.
     */
    @Query("{'targeted_vendors': ?0, 'status': {'$in': ['ACTIVE', 'COMPETITIVE']}}")
    Page<BidRequest> findByTargetedVendor(String vendorId, Pageable pageable);

    // =========================================================
    // LOCATION & DATE
    // =========================================================

    /**
     * Find bid requests by city.
     */
    @Query("{'event_details.venue_address.city': ?0, 'status': {'$in': ['ACTIVE', 'COMPETITIVE']}}")
    Page<BidRequest> findByCity(String city, Pageable pageable);

    /**
     * Find bid requests by event date range.
     */
    @Query("{'event_details.event_date': {'$gte': ?0, '$lte': ?1}}")
    List<BidRequest> findByEventDateBetween(Instant start, Instant end);
}
