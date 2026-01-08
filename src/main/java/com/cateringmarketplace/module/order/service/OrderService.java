package com.cateringmarketplace.module.order.service;

import com.cateringmarketplace.common.exception.BadRequestException;
import com.cateringmarketplace.common.exception.ForbiddenException;
import com.cateringmarketplace.common.exception.ResourceNotFoundException;
import com.cateringmarketplace.module.bid.model.BidRequest;
import com.cateringmarketplace.module.bid.model.BidRequest.BidRequestStatus;
import com.cateringmarketplace.module.bid.model.VendorBid;
import com.cateringmarketplace.module.bid.repository.BidRequestRepository;
import com.cateringmarketplace.module.bid.repository.VendorBidRepository;
import com.cateringmarketplace.module.order.dto.response.OrderResponse;
import com.cateringmarketplace.module.order.model.Order;
import com.cateringmarketplace.module.order.model.Order.*;
import com.cateringmarketplace.module.order.repository.OrderRepository;
import com.cateringmarketplace.module.vendor.model.Vendor;
import com.cateringmarketplace.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for order operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BidRequestRepository bidRequestRepository;
    private final VendorBidRepository vendorBidRepository;
    private final VendorRepository vendorRepository;

    @Value("${payment.token-percentage:25}")
    private double tokenPercentage;

    @Value("${payment.platform-fee-percentage:2}")
    private double platformFeePercentage;

    /**
     * Creates an order from an accepted bid.
     */
    @Transactional
    public OrderResponse createOrderFromBid(String bidRequestId, String userId) {
        log.info("Creating order from bid request: {} for user: {}", bidRequestId, userId);

        // Get bid request
        BidRequest bidRequest = bidRequestRepository.findByBidRequestId(bidRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid request not found"));

        // Validate ownership
        if (!bidRequest.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot create order for this bid request");
        }

        // Check if already has an order
        if (orderRepository.findByBidRequestId(bidRequestId).isPresent()) {
            throw new BadRequestException("ORDER_EXISTS", "Order already exists for this bid request");
        }

        // Check if bid is accepted and cooling period ended
        if (bidRequest.getStatus() != BidRequestStatus.COOLING &&
            bidRequest.getStatus() != BidRequestStatus.ACCEPTED) {
            throw new BadRequestException("INVALID_STATUS", "Bid request is not ready for order creation");
        }

        if (bidRequest.getAcceptedBid() == null) {
            throw new BadRequestException("NO_ACCEPTED_BID", "No bid has been accepted for this request");
        }

        // Get accepted bid
        VendorBid acceptedBid = vendorBidRepository.findByBidId(bidRequest.getAcceptedBid().getBidId())
                .orElseThrow(() -> new ResourceNotFoundException("Accepted bid not found"));

        // Get vendor
        Vendor vendor = vendorRepository.findByVendorId(acceptedBid.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // Create order
        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(userId)
                .bidRequestId(bidRequestId)
                .status(OrderStatus.PENDING_TOKEN_PAYMENT)
                .build();

        // Map event details from bid request
        if (bidRequest.getEventDetails() != null) {
            var ed = bidRequest.getEventDetails();
            Order.EventDetails eventDetails = Order.EventDetails.builder()
                    .eventType(ed.getEventType())
                    .eventName(ed.getEventName())
                    .eventDate(ed.getEventDate() != null ? ed.getEventDate().toLocalDate() : null)
                    .eventTime(ed.getEventStartTime())
                    .numberOfGuests(ed.getNumberOfGuests())
                    .build();

            if (ed.getVenueAddress() != null) {
                eventDetails.setVenueAddress(VenueAddress.builder()
                        .streetAddress(ed.getVenueAddress().getStreetAddress())
                        .city(ed.getVenueAddress().getCity())
                        .state(ed.getVenueAddress().getState())
                        .postalCode(ed.getVenueAddress().getPostalCode())
                        .country(ed.getVenueAddress().getCountry())
                        .gpsCoordinates(ed.getVenueAddress().getGpsCoordinates())
                        .build());
            }
            order.setEventDetails(eventDetails);
        }

        // Create vendor order
        VendorOrder vendorOrder = VendorOrder.builder()
                .vendorOrderId(UUID.randomUUID().toString())
                .vendorId(vendor.getVendorId())
                .vendorName(vendor.getBusinessName())
                .vendorStatus(VendorOrder.VendorOrderStatus.PENDING)
                .deliveryStatus(VendorOrder.DeliveryStatus.PENDING)
                .build();

        // Map items from bid
        if (acceptedBid.getItemizedPricing() != null) {
            vendorOrder.setItems(acceptedBid.getItemizedPricing().stream()
                    .map(ip -> OrderItem.builder()
                            .vendorItemId(ip.getVendorItemId())
                            .itemName(ip.getItemName())
                            .quantity(ip.getQuantity())
                            .pricePerPlate(ip.getPricePerPlate())
                            .totalPrice(ip.getTotalPrice())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Set vendor order pricing from bid
        if (acceptedBid.getQuotedPrice() != null) {
            vendorOrder.setSubtotal(acceptedBid.getQuotedPrice().getSubtotal());
            vendorOrder.setServiceCharge(acceptedBid.getQuotedPrice().getServiceCharge());
            vendorOrder.setTaxAmount(acceptedBid.getQuotedPrice().getTaxAmount());
            vendorOrder.setTotalAmount(acceptedBid.getQuotedPrice().getTotalAmount());
        }

        order.setVendorOrders(List.of(vendorOrder));

        // Calculate order pricing
        BigDecimal totalAmount = acceptedBid.getQuotedPrice() != null ?
                acceptedBid.getQuotedPrice().getTotalAmount() : BigDecimal.ZERO;
        BigDecimal platformFee = totalAmount.multiply(BigDecimal.valueOf(platformFeePercentage / 100))
                .setScale(2, RoundingMode.HALF_UP);

        order.setPricing(OrderPricing.builder()
                .currency("INR")
                .subtotal(acceptedBid.getQuotedPrice() != null ? acceptedBid.getQuotedPrice().getSubtotal() : BigDecimal.ZERO)
                .serviceCharges(acceptedBid.getQuotedPrice() != null ? acceptedBid.getQuotedPrice().getServiceCharge() : BigDecimal.ZERO)
                .taxAmount(acceptedBid.getQuotedPrice() != null ? acceptedBid.getQuotedPrice().getTaxAmount() : BigDecimal.ZERO)
                .platformFee(platformFee)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount.add(platformFee))
                .build());

        // Calculate token amount
        BigDecimal tokenAmount = totalAmount.add(platformFee)
                .multiply(BigDecimal.valueOf(tokenPercentage / 100))
                .setScale(2, RoundingMode.HALF_UP);

        order.setPaymentDetails(PaymentDetails.builder()
                .tokenAmount(tokenAmount)
                .tokenPaid(false)
                .totalPaid(BigDecimal.ZERO)
                .balanceDue(totalAmount.add(platformFee))
                .paymentStatus(PaymentDetails.PaymentStatus.TOKEN_PENDING)
                .build());

        // Map additional requirements as special instructions
        if (bidRequest.getAdditionalRequirements() != null) {
            order.setSpecialInstructions(bidRequest.getAdditionalRequirements().getSpecialInstructions());
        }

        order = orderRepository.save(order);

        // Update bid request status
        bidRequest.setStatus(BidRequestStatus.ACCEPTED);
        bidRequestRepository.save(bidRequest);

        log.info("Order created successfully: {}", order.getOrderId());

        // TODO: Send order confirmation notification

        return OrderResponse.fromEntity(order);
    }

    /**
     * Gets order by ID.
     */
    public OrderResponse getOrder(String orderId, String userId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Check access
        if (!order.getUserId().equals(userId) && !isVendorOfOrder(order, userId)) {
            throw new ForbiddenException("FORBIDDEN", "You don't have access to this order");
        }

        return OrderResponse.fromEntity(order);
    }

    /**
     * Gets orders for a user.
     */
    public Page<OrderResponse> getUserOrders(String userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(OrderResponse::fromEntity);
    }

    /**
     * Gets orders for a vendor.
     */
    public Page<OrderResponse> getVendorOrders(String vendorId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByVendorId(vendorId, pageable);
        return orders.map(OrderResponse::fromEntity);
    }

    /**
     * Gets all orders (admin only).
     */
    public Page<OrderResponse> getAllOrders(Pageable pageable, String status) {
        Page<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }
        return orders.map(OrderResponse::fromEntity);
    }

    /**
     * Updates order status.
     */
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus, String userId) {
        log.info("Updating order {} status to: {}", orderId, newStatus);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Validate state transition
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        // Set timestamps based on status
        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(Instant.now());
            case DELIVERED -> order.setDeliveredAt(Instant.now());
            case COMPLETED -> order.setCompletedAt(Instant.now());
        }

        order = orderRepository.save(order);
        log.info("Order status updated: {} -> {}", orderId, newStatus);

        // TODO: Send notification

        return OrderResponse.fromEntity(order);
    }

    /**
     * Cancels an order.
     */
    @Transactional
    public OrderResponse cancelOrder(String orderId, String reason, String userId) {
        log.info("Cancelling order: {} by user: {}", orderId, userId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN", "You cannot cancel this order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("CANNOT_CANCEL", "Cannot cancel a delivered or completed order");
        }

        // Calculate refund based on cancellation policy
        BigDecimal refundAmount = calculateRefundAmount(order);

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellation(Cancellation.builder()
                .isCancelled(true)
                .cancelledBy(userId)
                .cancelledByType("USER")
                .cancellationReason(reason)
                .cancelledAt(Instant.now())
                .refundAmount(refundAmount)
                .refundStatus(refundAmount.compareTo(BigDecimal.ZERO) > 0 ? "PENDING" : "NOT_APPLICABLE")
                .build());

        order = orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);

        // TODO: Process refund if applicable
        // TODO: Notify vendor

        return OrderResponse.fromEntity(order);
    }

    /**
     * Gets upcoming orders for a user.
     */
    public Page<OrderResponse> getUpcomingOrders(String userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.CONFIRMED, pageable);
        return orders.map(OrderResponse::fromEntity);
    }

    // ==================== HELPER METHODS ====================

    private boolean isVendorOfOrder(Order order, String userId) {
        // Check if user is a vendor in this order
        return order.getVendorOrders().stream()
                .anyMatch(vo -> vo.getVendorId() != null);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        // Define valid transitions
        List<OrderStatus> validNextStatuses = switch (current) {
            case PENDING_TOKEN_PAYMENT -> List.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
            case CONFIRMED -> List.of(OrderStatus.IN_PREPARATION, OrderStatus.CANCELLED);
            case IN_PREPARATION -> List.of(OrderStatus.READY_FOR_DELIVERY, OrderStatus.CANCELLED);
            case READY_FOR_DELIVERY -> List.of(OrderStatus.DELIVERING, OrderStatus.CANCELLED);
            case DELIVERING -> List.of(OrderStatus.DELIVERED);
            case DELIVERED -> List.of(OrderStatus.COMPLETED);
            case COMPLETED, CANCELLED -> List.of();
        };

        if (!validNextStatuses.contains(next)) {
            throw new BadRequestException("INVALID_TRANSITION",
                    "Cannot transition from " + current + " to " + next);
        }
    }

    private BigDecimal calculateRefundAmount(Order order) {
        if (order.getPaymentDetails() == null || order.getPaymentDetails().getTotalPaid() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPaid = order.getPaymentDetails().getTotalPaid();
        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Calculate days before event
        long daysBeforeEvent = 0;
        if (order.getEventDetails() != null && order.getEventDetails().getEventDate() != null) {
            daysBeforeEvent = ChronoUnit.DAYS.between(LocalDate.now(), order.getEventDetails().getEventDate());
        }

        // Apply cancellation policy
        double refundPercentage;
        if (daysBeforeEvent >= 30) {
            refundPercentage = 100;
        } else if (daysBeforeEvent >= 15) {
            refundPercentage = 75;
        } else if (daysBeforeEvent >= 7) {
            refundPercentage = 50;
        } else if (daysBeforeEvent >= 3) {
            refundPercentage = 25;
        } else {
            refundPercentage = 0;
        }

        return totalPaid.multiply(BigDecimal.valueOf(refundPercentage / 100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

