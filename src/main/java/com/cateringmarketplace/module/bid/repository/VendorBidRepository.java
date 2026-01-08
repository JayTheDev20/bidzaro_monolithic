package com.cateringmarketplace.module.bid.repository;

import com.cateringmarketplace.module.bid.model.VendorBid;
import com.cateringmarketplace.module.bid.model.VendorBid.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for VendorBid entity operations.
 */
@Repository
public interface VendorBidRepository extends MongoRepository<VendorBid, String> {

    /**
     * Find by bid ID.
     */
    Optional<VendorBid> findByBidId(String bidId);

    /**
     * Find bids by bid request ID.
     */
    List<VendorBid> findByBidRequestId(String bidRequestId);

    /**
     * Find bids by bid request ID with pagination.
     */
    Page<VendorBid> findByBidRequestId(String bidRequestId, Pageable pageable);

    /**
     * Find bid by bid request ID and vendor ID.
     */
    Optional<VendorBid> findByBidRequestIdAndVendorId(String bidRequestId, String vendorId);

    /**
     * Find bids by vendor ID.
     */
    Page<VendorBid> findByVendorId(String vendorId, Pageable pageable);

    /**
     * Find bids by vendor ID and status.
     */
    Page<VendorBid> findByVendorIdAndStatus(String vendorId, BidStatus status, Pageable pageable);

    /**
     * Find valid bids for a bid request (not expired or withdrawn).
     */
    @Query("{'bid_request_id': ?0, 'status': {'$in': ['SUBMITTED', 'REVISED']}}")
    List<VendorBid> findValidBidsForRequest(String bidRequestId);

    /**
     * Find the lowest bid for a request.
     */
    @Query("{'bid_request_id': ?0, 'status': {'$in': ['SUBMITTED', 'REVISED']}}")
    List<VendorBid> findBidsForRanking(String bidRequestId);

    /**
     * Find accepted bid for a request.
     */
    Optional<VendorBid> findByBidRequestIdAndStatus(String bidRequestId, BidStatus status);

    /**
     * Check if vendor has already bid on a request.
     */
    boolean existsByBidRequestIdAndVendorId(String bidRequestId, String vendorId);

    /**
     * Count bids for a request.
     */
    long countByBidRequestId(String bidRequestId);

    /**
     * Count bids by vendor.
     */
    long countByVendorId(String vendorId);

    /**
     * Find expired bids.
     */
    @Query("{'status': {'$in': ['SUBMITTED', 'REVISED']}, 'expires_at': {'$lt': ?0}}")
    List<VendorBid> findExpiredBids(Instant now);

    /**
     * Find bids by status.
     */
    Page<VendorBid> findByStatus(BidStatus status, Pageable pageable);

    /**
     * Delete bids by bid request ID.
     */
    void deleteByBidRequestId(String bidRequestId);

    /**
     * Count accepted bids.
     */
    @Query(value = "{'status': 'ACCEPTED'}", count = true)
    long countByStatusAccepted();
}

