package com.cateringmarketplace.module.order.dto.response;

import com.cateringmarketplace.module.order.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for order information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private String orderId;
    private String userId;
    private String bidRequestId;
    private EventDetailsResponse eventDetails;
    private List<VendorOrderResponse> vendorOrders;
    private PricingResponse pricing;
    private PaymentDetailsResponse paymentDetails;
    private ContactInfoResponse contactInfo;
    private String specialInstructions;
    private String status;
    private CancellationResponse cancellation;
    private Instant createdAt;
    private Instant confirmedAt;
    private Instant deliveredAt;
    private Instant completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDetailsResponse {
        private String eventType;
        private String eventName;
        private LocalDate eventDate;
        private String eventTime;
        private Integer numberOfGuests;
        private VenueAddressResponse venueAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueAddressResponse {
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorOrderResponse {
        private String vendorOrderId;
        private String vendorId;
        private String vendorName;
        private List<OrderItemResponse> items;
        private BigDecimal subtotal;
        private BigDecimal serviceCharge;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private String vendorStatus;
        private String deliveryStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String vendorItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal pricePerPlate;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingResponse {
        private String currency;
        private BigDecimal subtotal;
        private BigDecimal serviceCharges;
        private BigDecimal taxAmount;
        private BigDecimal platformFee;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetailsResponse {
        private BigDecimal tokenAmount;
        private Boolean tokenPaid;
        private Instant tokenPaidAt;
        private BigDecimal totalPaid;
        private BigDecimal balanceDue;
        private String paymentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoResponse {
        private String primaryContactName;
        private String primaryContactPhone;
        private String primaryContactEmail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancellationResponse {
        private Boolean isCancelled;
        private String cancellationReason;
        private Instant cancelledAt;
        private BigDecimal refundAmount;
        private String refundStatus;
    }

    /**
     * Creates an OrderResponse from an Order entity.
     */
    public static OrderResponse fromEntity(Order order) {
        if (order == null) return null;

        OrderResponseBuilder builder = OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .bidRequestId(order.getBidRequestId())
                .specialInstructions(order.getSpecialInstructions())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .confirmedAt(order.getConfirmedAt())
                .deliveredAt(order.getDeliveredAt())
                .completedAt(order.getCompletedAt());

        // Map event details
        if (order.getEventDetails() != null) {
            var ed = order.getEventDetails();
            EventDetailsResponse.EventDetailsResponseBuilder edBuilder = EventDetailsResponse.builder()
                    .eventType(ed.getEventType())
                    .eventName(ed.getEventName())
                    .eventDate(ed.getEventDate())
                    .eventTime(ed.getEventTime())
                    .numberOfGuests(ed.getNumberOfGuests());

            if (ed.getVenueAddress() != null) {
                edBuilder.venueAddress(VenueAddressResponse.builder()
                        .streetAddress(ed.getVenueAddress().getStreetAddress())
                        .city(ed.getVenueAddress().getCity())
                        .state(ed.getVenueAddress().getState())
                        .postalCode(ed.getVenueAddress().getPostalCode())
                        .country(ed.getVenueAddress().getCountry())
                        .build());
            }
            builder.eventDetails(edBuilder.build());
        }

        // Map vendor orders
        if (order.getVendorOrders() != null) {
            builder.vendorOrders(order.getVendorOrders().stream()
                    .map(vo -> VendorOrderResponse.builder()
                            .vendorOrderId(vo.getVendorOrderId())
                            .vendorId(vo.getVendorId())
                            .vendorName(vo.getVendorName())
                            .subtotal(vo.getSubtotal())
                            .serviceCharge(vo.getServiceCharge())
                            .taxAmount(vo.getTaxAmount())
                            .totalAmount(vo.getTotalAmount())
                            .vendorStatus(vo.getVendorStatus() != null ? vo.getVendorStatus().name() : null)
                            .deliveryStatus(vo.getDeliveryStatus() != null ? vo.getDeliveryStatus().name() : null)
                            .items(vo.getItems() != null ? vo.getItems().stream()
                                    .map(item -> OrderItemResponse.builder()
                                            .vendorItemId(item.getVendorItemId())
                                            .itemName(item.getItemName())
                                            .quantity(item.getQuantity())
                                            .pricePerPlate(item.getPricePerPlate())
                                            .totalPrice(item.getTotalPrice())
                                            .build())
                                    .toList() : null)
                            .build())
                    .toList());
        }

        // Map pricing
        if (order.getPricing() != null) {
            builder.pricing(PricingResponse.builder()
                    .currency(order.getPricing().getCurrency())
                    .subtotal(order.getPricing().getSubtotal())
                    .serviceCharges(order.getPricing().getServiceCharges())
                    .taxAmount(order.getPricing().getTaxAmount())
                    .platformFee(order.getPricing().getPlatformFee())
                    .discountAmount(order.getPricing().getDiscountAmount())
                    .totalAmount(order.getPricing().getTotalAmount())
                    .build());
        }

        // Map payment details
        if (order.getPaymentDetails() != null) {
            builder.paymentDetails(PaymentDetailsResponse.builder()
                    .tokenAmount(order.getPaymentDetails().getTokenAmount())
                    .tokenPaid(order.getPaymentDetails().getTokenPaid())
                    .tokenPaidAt(order.getPaymentDetails().getTokenPaidAt())
                    .totalPaid(order.getPaymentDetails().getTotalPaid())
                    .balanceDue(order.getPaymentDetails().getBalanceDue())
                    .paymentStatus(order.getPaymentDetails().getPaymentStatus() != null ?
                            order.getPaymentDetails().getPaymentStatus().name() : null)
                    .build());
        }

        // Map contact info
        if (order.getContactInfo() != null) {
            builder.contactInfo(ContactInfoResponse.builder()
                    .primaryContactName(order.getContactInfo().getPrimaryContactName())
                    .primaryContactPhone(order.getContactInfo().getPrimaryContactPhone())
                    .primaryContactEmail(order.getContactInfo().getPrimaryContactEmail())
                    .build());
        }

        // Map cancellation
        if (order.getCancellation() != null) {
            builder.cancellation(CancellationResponse.builder()
                    .isCancelled(order.getCancellation().getIsCancelled())
                    .cancellationReason(order.getCancellation().getCancellationReason())
                    .cancelledAt(order.getCancellation().getCancelledAt())
                    .refundAmount(order.getCancellation().getRefundAmount())
                    .refundStatus(order.getCancellation().getRefundStatus())
                    .build());
        }

        return builder.build();
    }
}

