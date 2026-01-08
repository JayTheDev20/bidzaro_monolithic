package com.cateringmarketplace.scheduler;

import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduler for order-related background tasks.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    /**
     * Auto-cancels orders with pending token payment after 24 hours.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cancelPendingPaymentOrders() {
        log.info("Checking for pending payment orders to cancel...");

        try {
            Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
            List<Order> pendingOrders = orderRepository.findPendingPaymentOrders(cutoff);

            for (Order order : pendingOrders) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setCancellation(Order.Cancellation.builder()
                        .isCancelled(true)
                        .cancelledByType("SYSTEM")
                        .cancellationReason("Token payment not received within 24 hours")
                        .cancelledAt(Instant.now())
                        .refundStatus("NOT_APPLICABLE")
                        .build());
                orderRepository.save(order);

                log.info("Auto-cancelled order due to pending payment: {}", order.getOrderId());

                // TODO: Send cancellation notification
            }

            log.info("Cancelled {} pending payment orders", pendingOrders.size());

        } catch (Exception e) {
            log.error("Error during pending payment order cancellation: {}", e.getMessage(), e);
        }
    }

    /**
     * Auto-completes delivered orders after 24 hours.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void autoCompleteDeliveredOrders() {
        log.info("Checking for orders to auto-complete...");

        try {
            Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
            List<Order> deliveredOrders = orderRepository.findOrdersForCompletion(cutoff);

            for (Order order : deliveredOrders) {
                order.setStatus(OrderStatus.COMPLETED);
                order.setCompletedAt(Instant.now());
                orderRepository.save(order);

                log.info("Auto-completed order: {}", order.getOrderId());

                // TODO: Send order completed notification
            }

            log.info("Auto-completed {} delivered orders", deliveredOrders.size());

        } catch (Exception e) {
            log.error("Error during order auto-completion: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends payment reminders for upcoming orders (7 days before event).
     */
    @Scheduled(cron = "0 0 9 * * *") // Every day at 9 AM
    public void sendPaymentReminders() {
        log.info("Sending payment reminders for upcoming orders...");

        // TODO: Implement payment reminder logic
        // Find orders with events in 7 days that still have balance due
        // Send payment reminder notification
    }
}

