package com.cateringmarketplace.module.bid.controller;

import com.cateringmarketplace.common.response.ApiResponse;
import com.cateringmarketplace.common.response.PageInfo;
import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import com.cateringmarketplace.module.bid.dto.request.CreateBidRequestDTO;
import com.cateringmarketplace.module.bid.dto.request.SubmitBidDTO;
import com.cateringmarketplace.module.bid.dto.response.BidRequestResponse;
import com.cateringmarketplace.module.bid.dto.response.VendorBidResponse;
import com.cateringmarketplace.module.bid.service.BidService;
import com.cateringmarketplace.module.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for bid operations.
 */
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bids", description = "Bid request and vendor bid management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BidController {

    private final BidService bidService;
    private final VendorService vendorService;

    // ==================== BID REQUEST ENDPOINTS ====================

    @PostMapping("/requests")
    @Operation(summary = "Create bid request", description = "Creates a new bid request for catering services")
    public ResponseEntity<ApiResponse<BidRequestResponse>> createBidRequest(
            @Valid @RequestBody CreateBidRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Creating bid request for user: {}", userDetails.getUserId());
        BidRequestResponse response = bidService.createBidRequest(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Bid request created successfully"));
    }

    @PostMapping("/requests/from-cart")
    @Operation(summary = "Create bid requests from cart", description = "Creates bid requests based on items in the user's cart")
    public ResponseEntity<ApiResponse<List<BidRequestResponse>>> createBidRequestsFromCart(
            @Valid @RequestBody CreateBidRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Creating bid requests from cart for user: {}", userDetails.getUserId());
        List<BidRequestResponse> responses = bidService.createBidRequestsFromCart(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(responses, "Bid requests created successfully from cart"));
    }

    @GetMapping("/requests")
    @Operation(summary = "Get user's bid requests", description = "Returns paginated list of user's bid requests")
    public ResponseEntity<ApiResponse<List<BidRequestResponse>>> getUserBidRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BidRequestResponse> requests = bidService.getUserBidRequests(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(
                requests.getContent(),
                "Bid requests retrieved",
                PageInfo.from(requests)
        ));
    }

    @GetMapping("/requests/{bidRequestId}")
    @Operation(summary = "Get bid request", description = "Returns bid request details by ID")
    public ResponseEntity<ApiResponse<BidRequestResponse>> getBidRequest(
            @PathVariable String bidRequestId) {
        BidRequestResponse response = bidService.getBidRequest(bidRequestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/requests/{bidRequestId}")
    @Operation(summary = "Cancel bid request", description = "Cancels a bid request")
    public ResponseEntity<ApiResponse<Void>> cancelBidRequest(
            @PathVariable String bidRequestId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        bidService.cancelBidRequest(bidRequestId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Bid request cancelled"));
    }

    @GetMapping("/requests/{bidRequestId}/bids")
    @Operation(summary = "Get bids for request", description = "Returns all bids for a bid request (owner only)")
    public ResponseEntity<ApiResponse<List<VendorBidResponse>>> getBidsForRequest(
            @PathVariable String bidRequestId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<VendorBidResponse> bids = bidService.getBidsForRequest(bidRequestId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(bids));
    }

    @PostMapping("/{bidId}/accept")
    @Operation(summary = "Accept bid", description = "Accepts a vendor's bid")
    public ResponseEntity<ApiResponse<BidRequestResponse>> acceptBid(
            @PathVariable String bidId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("User {} accepting bid: {}", userDetails.getUserId(), bidId);
        BidRequestResponse response = bidService.acceptBid(bidId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response, "Bid accepted. Cooling period started."));
    }

    // ==================== VENDOR BID ENDPOINTS ====================

    @PostMapping("/requests/{bidRequestId}/submit-bid")
    @Operation(summary = "Submit bid", description = "Submits a bid for a bid request (vendor only)")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorBidResponse>> submitBid(
            @PathVariable String bidRequestId,
            @Valid @RequestBody SubmitBidDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Get vendor ID for the user
        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        log.info("Vendor {} submitting bid for request: {}", vendorId, bidRequestId);
        VendorBidResponse response = bidService.submitBid(bidRequestId, request, vendorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Bid submitted successfully"));
    }

    @PutMapping("/{bidId}")
    @Operation(summary = "Revise bid", description = "Revises an existing bid (vendor only)")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<VendorBidResponse>> reviseBid(
            @PathVariable String bidId,
            @Valid @RequestBody SubmitBidDTO request,
            @RequestParam(required = false, defaultValue = "Price adjustment") String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        log.info("Vendor {} revising bid: {}", vendorId, bidId);
        VendorBidResponse response = bidService.reviseBid(bidId, request, vendorId, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Bid revised successfully"));
    }

    @DeleteMapping("/{bidId}")
    @Operation(summary = "Withdraw bid", description = "Withdraws a submitted bid (vendor only)")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<Void>> withdrawBid(
            @PathVariable String bidId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        bidService.withdrawBid(bidId, vendorId);
        return ResponseEntity.ok(ApiResponse.success(null, "Bid withdrawn"));
    }

    @GetMapping("/vendor/submitted")
    @Operation(summary = "Get vendor's bids", description = "Returns all bids submitted by vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<VendorBidResponse>>> getVendorSubmittedBids(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<VendorBidResponse> bids = bidService.getVendorBids(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                bids.getContent(),
                "Vendor bids retrieved",
                PageInfo.from(bids)
        ));
    }

    @GetMapping("/vendor/received")
    @Operation(summary = "Get bid requests for vendor", description = "Returns active bid requests vendor can bid on")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<BidRequestResponse>>> getVendorReceivedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String vendorId = vendorService.getVendorByUserId(userDetails.getUserId()).getVendorId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BidRequestResponse> requests = bidService.getVendorTargetedRequests(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                requests.getContent(),
                "Bid requests retrieved",
                PageInfo.from(requests)
        ));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active bid requests", description = "Returns all active bid requests (for vendors)")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<BidRequestResponse>>> getActiveBidRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BidRequestResponse> requests = bidService.getActiveBidRequests(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                requests.getContent(),
                "Active bid requests retrieved",
                PageInfo.from(requests)
        ));
    }
}
