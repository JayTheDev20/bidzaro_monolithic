package com.cateringmarketplace.scheduler;

import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.OrderStatus;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.auth.repository.UserRepository;
import com.cateringmarketplace.module.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
    private final UserRepository userRepository;
    private final EmailService emailService;

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

        try {
            LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);

            // Get all confirmed orders that still owe a balance
            List<Order> ordersWithBalance = orderRepository.findConfirmedOrdersWithBalanceDue();

            int remindersSent = 0;
            for (Order order : ordersWithBalance) {
                try {
                    // Only remind if event is within the next 7 days
                    if (order.getEventDetails() == null || order.getEventDetails().getEventDate() == null) continue;
                    LocalDate eventDate = order.getEventDetails().getEventDate();
                    long daysUntilEvent = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
                    if (daysUntilEvent < 0 || daysUntilEvent > 7) continue;

                    BigDecimal balanceDue = order.getPaymentDetails() != null
                            ? order.getPaymentDetails().getBalanceDue() : null;
                    if (balanceDue == null || balanceDue.compareTo(BigDecimal.ZERO) <= 0) continue;

                    final String eventDateStr = eventDate.toString();
                    userRepository.findByUserId(order.getUserId()).ifPresent(user -> {
                        String subject = "Payment Reminder – Balance Due for Your Event on " + eventDateStr;
                        String body = String.format(
                                "<p>Dear %s,</p>" +
                                "<p>This is a friendly reminder that your order <strong>%s</strong> has a balance due of " +
                                "<strong>%s %s</strong> for the event on <strong>%s</strong> (%d day(s) away).</p>" +
                                "<p>Please ensure payment is completed before the event date to avoid cancellation.</p>" +
                                "<p>Best regards,<br>The Bidzaro Team</p>",
                                user.getFirstName() != null ? user.getFirstName() : "Customer",
                                order.getOrderId(),
                                order.getPaymentDetails().getBalanceDue(),
                                order.getPricing() != null ? order.getPricing().getCurrency() : "INR",
                                eventDateStr,
                                daysUntilEvent
                        );
                        emailService.sendSimpleEmail(user.getEmail(), subject, body, "USER");
                    });
                    remindersSent++;
                } catch (Exception e) {
                    log.error("Failed to send payment reminder for order {}: {}", order.getOrderId(), e.getMessage());
                }
            }
            log.info("Payment reminders sent: {}", remindersSent);

        } catch (Exception e) {
            log.error("Error during payment reminder job: {}", e.getMessage(), e);
        }
    }
}

