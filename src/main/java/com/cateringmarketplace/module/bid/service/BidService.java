package com.cateringmarketplace.module.bid.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO;
import com.cateringmarketplace.module.bid.dto.request.SubmitBidDTO;
import com.cateringmarketplace.module.bid.dto.response.BidRequestResponse;
import com.cateringmarketplace.module.bid.dto.response.VendorBidResponse;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.model.BidRequest.*;
import com.cateringmarketplace.module.bid.model.VendorBid;
import com.cateringmarketplace.module.bid.model.VendorBid.BidStatus;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for bid operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

    private final BidRequestRepository bidRequestRepository;
    private final VendorBidRepository vendorBidRepository;
    private final VendorRepository vendorRepository;

    @Value("${bidding.competitive-period-hours:72}")
    private int competitivePeriodHours;

    @Value("${bidding.cooling-period-hours:24}")
    private int coolingPeriodHours;

    @Value("${bidding.bid-expiry-hours:168}")
    private int bidExpiryHours;

    @Value("${bidding.max-bid-revisions:5}")
    private int maxBidRevisions;

    // ==================== BID REQUEST OPERATIONS ====================

    /**
     * Creates a new bid request.
     */
    @Transactional
    public BidRequestResponse createBidRequest(CreateBidRequestDTO dto, String userId) {
        log.info("Creating bid request for user: {}", userId);

        BidRequest request = BidRequest.builder()
                .userId(userId)
                .targetedVendors(dto.getTargetedVendors())
                .status(BidRequestStatus.ACTIVE)
                .build();

        // Map event details
        if (dto.getEventDetails() != null) {
            var ed = dto.getEventDetails();
            EventDetails.EventDetailsBuilder edBuilder = EventDetails.builder()
                    .eventType(ed.getEventType())
                    .eventName(ed.getEventName())
                    .eventDate(ed.getEventDate())
                    .eventStartTime(ed.getEventStartTime())
                    .eventEndTime(ed.getEventEndTime())
                    .numberOfGuests(ed.getNumberOfGuests());

            if (ed.getVenueAddress() != null) {
                VenueAddress.VenueAddressBuilder vaBuilder = VenueAddress.builder()
                        .streetAddress(ed.getVenueAddress().getStreetAddress())
                        .city(ed.getVenueAddress().getCity())
                        .state(ed.getVenueAddress().getState())
                        .postalCode(ed.getVenueAddress().getPostalCode())
                        .country(ed.getVenueAddress().getCountry());

                if (ed.getVenueAddress().getLatitude() != null &&
                    ed.getVenueAddress().getLongitude() != null) {
                    vaBuilder.gpsCoordinates(new GeoJsonPoint(
                            ed.getVenueAddress().getLongitude(),
                            ed.getVenueAddress().getLatitude()));
                }
                edBuilder.venueAddress(vaBuilder.build());
            }
            request.setEventDetails(edBuilder.build());
        }

        // Map menu items
        if (dto.getMenuItems() != null) {
            request.setMenuItems(dto.getMenuItems().stream()
                    .map(mi -> RequestedMenuItem.builder()
                            .vendorItemId(mi.getVendorItemId())
                            .masterItemId(mi.getMasterItemId())
                            .itemName(mi.getItemName())
                            .quantity(mi.getQuantity())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Map additional requirements
        if (dto.getAdditionalRequirements() != null) {
            var ar = dto.getAdditionalRequirements();
            request.setAdditionalRequirements(AdditionalRequirements.builder()
                    .serviceStaffNeeded(ar.getServiceStaffNeeded())
                    .numberOfStaff(ar.getNumberOfStaff())
                    .decorationNeeded(ar.getDecorationNeeded())
                    .liveCounters(ar.getLiveCounters())
                    .specialInstructions(ar.getSpecialInstructions())
                    .build());
        }

        // Map budget
        if (dto.getBudget() != null) {
            request.setBudget(Budget.builder()
                    .currency(dto.getBudget().getCurrency() != null ? dto.getBudget().getCurrency() : "INR")
                    .estimatedBudget(dto.getBudget().getEstimatedBudget())
                    .budgetRange(dto.getBudget().getBudgetRange())
                    .build());
        }

        // Set competitive period
        Instant now = Instant.now();
        request.setCompetitivePeriod(CompetitivePeriod.builder()
                .startTime(now)
                .endTime(now.plus(competitivePeriodHours, ChronoUnit.HOURS))
                .status(PeriodStatus.ACTIVE)
                .build());

        request.setExpiresAt(now.plus(competitivePeriodHours, ChronoUnit.HOURS));

        request = bidRequestRepository.save(request);
        log.info("Bid request created with ID: {}", request.getBidRequestId());

        // TODO: Notify targeted vendors or vendors in service area

        return BidRequestResponse.fromEntity(request);
    }

    /**
     * Gets bid request by ID.
     */
    public BidRequestResponse getBidRequest(String bidRequestId) {
        BidRequest request = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));
        return BidRequestResponse.fromEntity(request);
    }

    /**
     * Gets bid requests for a user.
     */
    public Page<BidRequestResponse> getUserBidRequests(String userId, Pageable pageable) {
        Page<BidRequest> requests = bidRequestRepository.findByUserId(userId, pageable);
        return requests.map(BidRequestResponse::fromEntity);
    }

    /**
     * Gets active bid requests for vendors.
     */
    public Page<BidRequestResponse> getActiveBidRequests(Pageable pageable) {
        Page<BidRequest> requests = bidRequestRepository.findActiveBidRequests(Instant.now(), pageable);
        return requests.map(BidRequestResponse::fromEntity);
    }

    /**
     * Gets bid requests targeted to a vendor.
     */
    public Page<BidRequestResponse> getVendorTargetedRequests(String vendorId, Pageable pageable) {
        Page<BidRequest> requests = bidRequestRepository.findByTargetedVendor(vendorId, pageable);
        return requests.map(BidRequestResponse::fromEntity);
    }

    /**
     * Cancels a bid request.
     */
    @Transactional
    public void cancelBidRequest(String bidRequestId, String userId) {
        log.info("Cancelling bid request: {} by user: {}", bidRequestId, userId);

        BidRequest request = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        if (!request.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot cancel this bid request");
        }

        if (request.getStatus() == BidRequestStatus.ACCEPTED) {
            throw new BadRequestException("CANNOT_CANCEL", "Cannot cancel an accepted bid request");
        }

        request.setStatus(BidRequestStatus.CANCELLED);
        bidRequestRepository.save(request);

        // Notify vendors who submitted bids
        log.info("Bid request cancelled: {}", bidRequestId);
    }

    // ==================== VENDOR BID OPERATIONS ====================

    /**
     * Submits a bid from a vendor.
     */
    @Transactional
    public VendorBidResponse submitBid(String bidRequestId, SubmitBidDTO dto, String vendorId) {
        log.info("Vendor {} submitting bid for request: {}", vendorId, bidRequestId);

        // Get bid request
        BidRequest request = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        // Validate request is accepting bids
        if (!request.canAcceptBids()) {
            throw new BadRequestException("NOT_ACCEPTING_BIDS", "This bid request is no longer accepting bids");
        }

        // Check if vendor already submitted a bid
        if (vendorBidRepository.existsByBidRequestIdAndVendorId(bidRequestId, vendorId)) {
            throw new ConflictException("BID_EXISTS", "You have already submitted a bid for this request");
        }

        // Get vendor info
        Vendor vendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (!vendor.isActive()) {
            throw new ForbiddenException("VENDOR_INACTIVE", "Your vendor account is not active");
        }

        // Create bid
        VendorBid bid = VendorBid.builder()
                .bidId(UUID.randomUUID().toString())
                .bidRequestId(bidRequestId)
                .vendorId(vendorId)
                .vendorName(vendor.getBusinessName())
                .termsAndConditions(dto.getTermsAndConditions())
                .validityPeriodHours(dto.getValidityPeriodHours() != null ? dto.getValidityPeriodHours() : bidExpiryHours)
                .status(BidStatus.SUBMITTED)
                .submittedAt(Instant.now())
                .expiresAt(Instant.now().plus(bidExpiryHours, ChronoUnit.HOURS))
                .build();

        // Map quoted price
        if (dto.getQuotedPrice() != null) {
            bid.setQuotedPrice(VendorBid.QuotedPrice.builder()
                    .currency(dto.getQuotedPrice().getCurrency() != null ? dto.getQuotedPrice().getCurrency() : "INR")
                    .subtotal(dto.getQuotedPrice().getSubtotal())
                    .serviceCharge(dto.getQuotedPrice().getServiceCharge())
                    .taxPercentage(dto.getQuotedPrice().getTaxPercentage())
                    .taxAmount(dto.getQuotedPrice().getTaxAmount())
                    .totalAmount(dto.getQuotedPrice().getTotalAmount())
                    .build());
        }

        // Map itemized pricing
        if (dto.getItemizedPricing() != null) {
            bid.setItemizedPricing(dto.getItemizedPricing().stream()
                    .map(ip -> VendorBid.ItemizedPrice.builder()
                            .vendorItemId(ip.getVendorItemId())
                            .itemName(ip.getItemName())
                            .quantity(ip.getQuantity())
                            .pricePerPlate(ip.getPricePerPlate())
                            .totalPrice(ip.getTotalPrice())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Map delivery details
        if (dto.getDeliveryDetails() != null) {
            bid.setDeliveryDetails(VendorBid.DeliveryDetails.builder()
                    .estimatedSetupTime(dto.getDeliveryDetails().getEstimatedSetupTime())
                    .foodReadyTime(dto.getDeliveryDetails().getFoodReadyTime())
                    .cleanupTime(dto.getDeliveryDetails().getCleanupTime())
                    .build());
        }

        // Map staff provided
        if (dto.getStaffProvided() != null) {
            bid.setStaffProvided(VendorBid.StaffProvided.builder()
                    .chefs(dto.getStaffProvided().getChefs())
                    .servers(dto.getStaffProvided().getServers())
                    .cleaners(dto.getStaffProvided().getCleaners())
                    .build());
        }

        bid = vendorBidRepository.save(bid);

        // Update bid request
        request.incrementBidsReceived();
        if (bid.getQuotedPrice() != null) {
            request.updateLowestBid(bid.getQuotedPrice().getTotalAmount());
        }

        // Check if we should move to competitive status
        if (request.getTotalBidsReceived() >= 3 && request.getStatus() == BidRequestStatus.ACTIVE) {
            request.setStatus(BidRequestStatus.COMPETITIVE);
        }
        bidRequestRepository.save(request);

        // Update bid rankings
        updateBidRankings(bidRequestId);

        log.info("Bid submitted successfully: {}", bid.getBidId());

        // TODO: Notify user of new bid

        return VendorBidResponse.fromEntity(bid);
    }

    /**
     * Updates/revises a bid.
     */
    @Transactional
    public VendorBidResponse reviseBid(String bidId, SubmitBidDTO dto, String vendorId, String reason) {
        log.info("Vendor {} revising bid: {}", vendorId, bidId);

        VendorBid bid = vendorBidRepository.findByBidId(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        // Validate ownership
        if (!bid.getVendorId().equals(vendorId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot modify this bid");
        }

        // Check if bid can be revised
        if (!bid.isActive()) {
            throw new BadRequestException("BID_INACTIVE", "This bid cannot be revised");
        }

        if (bid.getRevisionCount() >= maxBidRevisions) {
            throw new BadRequestException("MAX_REVISIONS", "Maximum revisions reached for this bid");
        }

        // Save revision history
        BigDecimal previousAmount = bid.getTotalAmount();
        bid.addRevision(previousAmount, reason);

        // Update quoted price
        if (dto.getQuotedPrice() != null) {
            bid.setQuotedPrice(VendorBid.QuotedPrice.builder()
                    .currency(dto.getQuotedPrice().getCurrency())
                    .subtotal(dto.getQuotedPrice().getSubtotal())
                    .serviceCharge(dto.getQuotedPrice().getServiceCharge())
                    .taxPercentage(dto.getQuotedPrice().getTaxPercentage())
                    .taxAmount(dto.getQuotedPrice().getTaxAmount())
                    .totalAmount(dto.getQuotedPrice().getTotalAmount())
                    .build());
        }

        bid = vendorBidRepository.save(bid);

        // Update bid request lowest amount
        BidRequest request = bidRequestRepository.findByBidRequestId(bid.getBidRequestId())
                .orElse(null);
        if (request != null && bid.getQuotedPrice() != null) {
            request.updateLowestBid(bid.getQuotedPrice().getTotalAmount());
            bidRequestRepository.save(request);
        }

        // Update rankings
        updateBidRankings(bid.getBidRequestId());

        log.info("Bid revised: {}", bidId);

        return VendorBidResponse.fromEntity(bid);
    }

    /**
     * Withdraws a bid.
     */
    @Transactional
    public void withdrawBid(String bidId, String vendorId) {
        log.info("Vendor {} withdrawing bid: {}", vendorId, bidId);

        VendorBid bid = vendorBidRepository.findByBidId(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        if (!bid.getVendorId().equals(vendorId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot withdraw this bid");
        }

        if (bid.getStatus() == BidStatus.ACCEPTED) {
            throw new BadRequestException("CANNOT_WITHDRAW", "Cannot withdraw an accepted bid");
        }

        bid.setStatus(BidStatus.WITHDRAWN);
        vendorBidRepository.save(bid);

        // Update rankings
        updateBidRankings(bid.getBidRequestId());

        log.info("Bid withdrawn: {}", bidId);
    }

    /**
     * Accepts a bid.
     */
    @Transactional
    public BidRequestResponse acceptBid(String bidId, String userId) {
        log.info("User {} accepting bid: {}", userId, bidId);

        VendorBid bid = vendorBidRepository.findByBidId(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        BidRequest request = bidRequestRepository.findByBidRequestId(bid.getBidRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        // Validate ownership
        if (!request.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot accept bids for this request");
        }

        // Validate request status
        if (!request.canAcceptBids()) {
            throw new BadRequestException("CANNOT_ACCEPT", "This request is not accepting bids");
        }

        // Validate bid status
        if (!bid.isActive()) {
            throw new BadRequestException("BID_INACTIVE", "This bid is no longer active");
        }

        // Accept the bid
        bid.setStatus(BidStatus.ACCEPTED);
        vendorBidRepository.save(bid);

        // Update request
        request.setAcceptedBid(AcceptedBid.builder()
                .bidId(bidId)
                .vendorId(bid.getVendorId())
                .acceptedAt(Instant.now())
                .coolingPeriodEnd(Instant.now().plus(coolingPeriodHours, ChronoUnit.HOURS))
                .build());
        request.setStatus(BidRequestStatus.COOLING);
        request = bidRequestRepository.save(request);

        // Reject other bids
        List<VendorBid> otherBids = vendorBidRepository.findValidBidsForRequest(bid.getBidRequestId());
        for (VendorBid otherBid : otherBids) {
            if (!otherBid.getBidId().equals(bidId)) {
                otherBid.setStatus(BidStatus.REJECTED);
                vendorBidRepository.save(otherBid);
                // TODO: Notify vendor of rejection
            }
        }

        log.info("Bid accepted: {}. Entering cooling period.", bidId);

        // TODO: Notify winning vendor
        // TODO: Schedule cooling period end check

        return BidRequestResponse.fromEntity(request);
    }

    /**
     * Gets bids for a bid request.
     */
    public List<VendorBidResponse> getBidsForRequest(String bidRequestId, String userId) {
        BidRequest request = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        // Only the owner can see all bids
        if (!request.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot view bids for this request");
        }

        List<VendorBid> bids = vendorBidRepository.findByBidRequestId(bidRequestId);
        return bids.stream()
                .map(VendorBidResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Gets vendor's submitted bids.
     */
    public Page<VendorBidResponse> getVendorBids(String vendorId, Pageable pageable) {
        Page<VendorBid> bids = vendorBidRepository.findByVendorId(vendorId, pageable);
        return bids.map(VendorBidResponse::fromEntity);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Updates bid rankings for a request.
     */
    private void updateBidRankings(String bidRequestId) {
        List<VendorBid> bids = vendorBidRepository.findBidsForRanking(bidRequestId);

        // Sort by total amount ascending
        bids.sort(Comparator.comparing(VendorBid::getTotalAmount));

        // Update rankings
        for (int i = 0; i < bids.size(); i++) {
            VendorBid bid = bids.get(i);
            bid.setRank(i + 1);
            bid.setIsLowest(i == 0);
            vendorBidRepository.save(bid);
        }
    }
}

