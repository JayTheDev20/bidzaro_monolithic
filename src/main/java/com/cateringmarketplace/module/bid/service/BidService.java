package com.cateringmarketplace.module.bid.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ConflictException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO;
import com.cateringmarketplace.module.bid.dto.request.SubmitBidDTO;
import com.cateringmarketplace.module.bid.dto.request.UpdateBidRequestDTO;
import com.cateringmarketplace.module.bid.dto.response.BidRequestResponse;
import com.cateringmarketplace.module.bid.dto.response.VendorBidResponse;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.model.BidRequest.*;
import com.cateringmarketplace.module.bid.model.VendorBid;
import com.cateringmarketplace.module.bid.model.VendorBid.BidStatus;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.cart.model.CartItem;
import com.cateringmarketplace.module.cart.repository.CartRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private final CartRepository cartRepository;
    private final EmailService emailService;

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
     * Creates bid requests from the user's cart.
     * Groups items by vendor and creates a separate bid request for each vendor.
     */
    @Transactional
    public List<BidRequestResponse> createBidRequestsFromCart(CreateBidRequestDTO dto, String userId) {
        log.info("Creating bid requests from cart for user: {}", userId);

        // 1. Fetch cart items
        List<CartItem> cartItems = cartRepository.findByUserIdOrderByAddedAtDesc(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("CART_EMPTY", "Cannot create bid request from empty cart");
        }

        // 2. Group items by Vendor
        Map<String, List<CartItem>> itemsByVendor = cartItems.stream()
                .collect(Collectors.groupingBy(CartItem::getVendorId));

        List<BidRequestResponse> createdRequests = new ArrayList<>();

        // 3. Create a Bid Request for each vendor
        for (Map.Entry<String, List<CartItem>> entry : itemsByVendor.entrySet()) {
            String vendorId = entry.getKey();
            List<CartItem> vendorItems = entry.getValue();

            // Create DTO for this specific request
            CreateBidRequestDTO vendorRequestDTO = CreateBidRequestDTO.builder()
                    .eventDetails(dto.getEventDetails())
                    .additionalRequirements(dto.getAdditionalRequirements())
                    .budget(dto.getBudget())
                    .targetedVendors(Collections.singletonList(vendorId)) // Target ONLY this vendor
                    .menuItems(vendorItems.stream()
                            .map(item -> CreateBidRequestDTO.MenuItemDTO.builder()
                                    .vendorItemId(item.getVendorItemId())
                                    .masterItemId(null) // Can be populated if needed, but vendorItemId is key
                                    .itemName(item.getItemName())
                                    .quantity(item.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            // Call the standard create method
            createdRequests.add(createBidRequest(vendorRequestDTO, userId));
        }

        // 4. Clear the cart
        cartRepository.deleteByUserId(userId);
        log.info("Cart cleared for user: {}", userId);

        return createdRequests;
    }

    /**
     * Updates an existing bid request.
     */
    @Transactional
    public BidRequestResponse updateBidRequest(String bidRequestId, UpdateBidRequestDTO dto, String userId) {
        log.info("Updating bid request: {} for user: {}", bidRequestId, userId);

        BidRequest request = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        if (!request.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot update this bid request");
        }

        if (request.getStatus() == BidRequestStatus.ACCEPTED || request.getStatus() == BidRequestStatus.CANCELLED) {
            throw new BadRequestException("CANNOT_UPDATE", "Cannot update a bid request that is accepted or cancelled");
        }

        // Update Event Details
        if (dto.getEventDetails() != null) {
            var ed = dto.getEventDetails();
            EventDetails currentEd = request.getEventDetails();
            
            if (ed.getEventType() != null) currentEd.setEventType(ed.getEventType());
            if (ed.getEventName() != null) currentEd.setEventName(ed.getEventName());
            if (ed.getEventDate() != null) currentEd.setEventDate(ed.getEventDate());
            if (ed.getEventStartTime() != null) currentEd.setEventStartTime(ed.getEventStartTime());
            if (ed.getEventEndTime() != null) currentEd.setEventEndTime(ed.getEventEndTime());
            if (ed.getNumberOfGuests() != null) currentEd.setNumberOfGuests(ed.getNumberOfGuests());

            if (ed.getVenueAddress() != null) {
                VenueAddress currentVa = currentEd.getVenueAddress() != null ? currentEd.getVenueAddress() : new VenueAddress();
                if (ed.getVenueAddress().getStreetAddress() != null) currentVa.setStreetAddress(ed.getVenueAddress().getStreetAddress());
                if (ed.getVenueAddress().getCity() != null) currentVa.setCity(ed.getVenueAddress().getCity());
                if (ed.getVenueAddress().getState() != null) currentVa.setState(ed.getVenueAddress().getState());
                if (ed.getVenueAddress().getPostalCode() != null) currentVa.setPostalCode(ed.getVenueAddress().getPostalCode());
                if (ed.getVenueAddress().getCountry() != null) currentVa.setCountry(ed.getVenueAddress().getCountry());
                
                if (ed.getVenueAddress().getLatitude() != null && ed.getVenueAddress().getLongitude() != null) {
                    currentVa.setGpsCoordinates(new GeoJsonPoint(
                            ed.getVenueAddress().getLongitude(),
                            ed.getVenueAddress().getLatitude()));
                }
                currentEd.setVenueAddress(currentVa);
            }
            request.setEventDetails(currentEd);
        }

        // Update Budget
        if (dto.getBudget() != null) {
            Budget currentBudget = request.getBudget() != null ? request.getBudget() : new Budget();
            if (dto.getBudget().getCurrency() != null) currentBudget.setCurrency(dto.getBudget().getCurrency());
            if (dto.getBudget().getEstimatedBudget() != null) currentBudget.setEstimatedBudget(dto.getBudget().getEstimatedBudget());
            if (dto.getBudget().getBudgetRange() != null) currentBudget.setBudgetRange(dto.getBudget().getBudgetRange());
            request.setBudget(currentBudget);
        }

        // Update Additional Requirements
        if (dto.getAdditionalRequirements() != null) {
            AdditionalRequirements currentAr = request.getAdditionalRequirements() != null ? request.getAdditionalRequirements() : new AdditionalRequirements();
            if (dto.getAdditionalRequirements().getServiceStaffNeeded() != null) currentAr.setServiceStaffNeeded(dto.getAdditionalRequirements().getServiceStaffNeeded());
            if (dto.getAdditionalRequirements().getNumberOfStaff() != null) currentAr.setNumberOfStaff(dto.getAdditionalRequirements().getNumberOfStaff());
            if (dto.getAdditionalRequirements().getDecorationNeeded() != null) currentAr.setDecorationNeeded(dto.getAdditionalRequirements().getDecorationNeeded());
            if (dto.getAdditionalRequirements().getLiveCounters() != null) currentAr.setLiveCounters(dto.getAdditionalRequirements().getLiveCounters());
            if (dto.getAdditionalRequirements().getSpecialInstructions() != null) currentAr.setSpecialInstructions(dto.getAdditionalRequirements().getSpecialInstructions());
            request.setAdditionalRequirements(currentAr);
        }

        request = bidRequestRepository.save(request);
        log.info("Bid request updated: {}", bidRequestId);

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

        // Calculate advance amount if percentage is provided
        BigDecimal advancePercentage = dto.getAdvancePercentage();
        BigDecimal requiredAdvanceAmount = null;
        if (advancePercentage != null && dto.getQuotedPrice() != null && dto.getQuotedPrice().getTotalAmount() != null) {
            requiredAdvanceAmount = dto.getQuotedPrice().getTotalAmount()
                    .multiply(advancePercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Create bid
        VendorBid bid = VendorBid.builder()
                .bidId(UUID.randomUUID().toString())
                .bidRequestId(bidRequestId)
                .vendorId(vendorId)
                .vendorName(vendor.getBusinessName())
                .termsAndConditions(dto.getTermsAndConditions())
                .validityPeriodHours(dto.getValidityPeriodHours() != null ? dto.getValidityPeriodHours() : bidExpiryHours)
                .advancePercentage(advancePercentage)
                .requiredAdvanceAmount(requiredAdvanceAmount)
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

        // Update advance percentage and amount
        if (dto.getAdvancePercentage() != null) {
            bid.setAdvancePercentage(dto.getAdvancePercentage());
            if (bid.getQuotedPrice() != null && bid.getQuotedPrice().getTotalAmount() != null) {
                BigDecimal requiredAdvanceAmount = bid.getQuotedPrice().getTotalAmount()
                        .multiply(dto.getAdvancePercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                bid.setRequiredAdvanceAmount(requiredAdvanceAmount);
            }
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
                .coolingPeriodEnd(Instant.now().plus(24, ChronoUnit.HOURS)) // 24 hours for token payment
                .build());
        request.setStatus(BidRequestStatus.PENDING_TOKEN_PAYMENT); // Changed from COOLING
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

        log.info("Bid accepted: {}. Waiting for token payment.", bidId);

        return BidRequestResponse.fromEntity(request);
    }

    /**
     * Confirms token payment for a bid.
     * Called by Payment Service when token payment is successful.
     */
    @Transactional
    public void confirmBidPayment(String bidId) {
        log.info("Confirming token payment for bid: {}", bidId);

        VendorBid bid = vendorBidRepository.findByBidId(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        BidRequest request = bidRequestRepository.findByBidRequestId(bid.getBidRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        if (request.getStatus() != BidRequestStatus.PENDING_TOKEN_PAYMENT) {
            log.warn("Payment confirmed for bid request {} but status is {}", request.getBidRequestId(), request.getStatus());
            // We might still want to proceed if it's already ACCEPTED (duplicate event)
            if (request.getStatus() == BidRequestStatus.ACCEPTED) return;
        }

        // Move to ACCEPTED status (Ready for Order Creation)
        request.setStatus(BidRequestStatus.ACCEPTED);
        bidRequestRepository.save(request);

        log.info("Bid request {} status updated to ACCEPTED after payment.", request.getBidRequestId());
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
