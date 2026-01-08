package com.cateringmarketplace.scheduler;

import com.cateringmarketplace.module.promo.service.PromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for promo code maintenance.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromoExpiryScheduler {

    private final PromoService promoService;

    /**
     * Expires old promo codes daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expirePromos() {
        log.info("Running promo expiry job");
        promoService.expireOldPromos();
    }
}

