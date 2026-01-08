package com.cateringmarketplace.scheduler;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.model.BidRequest.BidRequestStatus;
import com.cateringmarketplace.module.bid.model.VendorBid;
import com.cateringmarketplace.module.bid.model.VendorBid.BidStatus;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;

/**
 * Scheduler for bid-related background tasks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BidExpiryScheduler {

    private final BidRequestRepository bidRequestRepository;
    private final VendorBidRepository vendorBidRepository;

    // =========================================================
    // BID REQUEST EXPIRY
    // =========================================================

    /**
     * Checks and expires old bid requests every hour.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void expireBidRequests() {

        log.info("Running bid request expiry check...");

        try {
            List<BidRequest> expiredRequests =
                    bidRequestRepository.findExpiredBidRequests(Instant.now());

            for (BidRequest request : expiredRequests) {

                request.setStatus(BidRequestStatus.EXPIRED);

                if (request.getCompetitivePeriod() != null) {
                    request.getCompetitivePeriod()
                            .setStatus(BidRequest.PeriodStatus.ENDED);
                }

                bidRequestRepository.save(request);

                // Expire all bids for this request
                List<VendorBid> bids =
                        vendorBidRepository.findValidBidsForRequest(
                                request.getBidRequestId());

                for (VendorBid bid : bids) {
                    bid.setStatus(BidStatus.EXPIRED);
                    vendorBidRepository.save(bid);
                }

                log.info("Expired bid request: {}", request.getBidRequestId());
            }

            log.info("Expired {} bid requests", expiredRequests.size());

        } catch (Exception e) {
            log.error("Error during bid request expiry: {}", e.getMessage(), e);
        }
    }

    // =========================================================
    // COOLING PERIOD
    // =========================================================

    /**
     * Checks and processes cooling period end every 15 minutes.
     */
    @Scheduled(cron = "0 */15 * * * *") // Every 15 minutes
    public void processCoolingPeriodEnd() {

        log.info("Checking for cooling period ended requests...");

        try {
            List<BidRequest> coolingEndedRequests =
                    bidRequestRepository.findCoolingPeriodEndedRequests(
                            Instant.now());

            for (BidRequest request : coolingEndedRequests) {

                // Transition to accepted status
                request.setStatus(BidRequestStatus.ACCEPTED);
                bidRequestRepository.save(request);

                log.info(
                        "Cooling period ended for request: {}. Ready for order creation.",
                        request.getBidRequestId()
                );

                // TODO: Send notification to user to complete order
            }

            log.info(
                    "Processed {} cooling period ended requests",
                    coolingEndedRequests.size()
            );

        } catch (Exception e) {
            log.error("Error during cooling period check: {}", e.getMessage(), e);
        }
    }

    // =========================================================
    // VENDOR BID EXPIRY
    // =========================================================

    /**
     * Expires old vendor bids daily.
     */
    @Scheduled(cron = "0 0 3 * * *") // Every day at 3 AM
    public void expireVendorBids() {

        log.info("Running vendor bid expiry check...");

        try {
            List<VendorBid> expiredBids =
                    vendorBidRepository.findExpiredBids(Instant.now());

            for (VendorBid bid : expiredBids) {
                bid.setStatus(BidStatus.EXPIRED);
                vendorBidRepository.save(bid);
            }

            log.info("Expired {} vendor bids", expiredBids.size());

        } catch (Exception e) {
            log.error("Error during vendor bid expiry: {}", e.getMessage(), e);
        }
    }
}
