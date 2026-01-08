package com.cateringmarketplace.module.support.dto.response;

import com.cateringmarketplace.module.support.model.Ticket;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for support ticket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketResponse {

    private String ticketId;
    private String ticketNumber;
    private String createdBy;
    private String createdByName;
    private String category;
    private String subcategory;
    private String priority;
    private String subject;
    private String description;
    private RelatedEntitiesDTO relatedEntities;
    private String assignedTo;
    private Instant assignedAt;
    private String status;
    private SlaDTO sla;
    private ResolutionDTO resolution;
    private CustomerSatisfactionDTO customerSatisfaction;
    private Instant createdAt;
    private Instant closedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedEntitiesDTO {
        private String orderId;
        private String vendorId;
        private String paymentId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlaDTO {
        private Instant firstResponseDue;
        private Instant resolutionDue;
        private Instant firstResponseAt;
        private Instant resolvedAt;
        private Boolean slaBreached;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResolutionDTO {
        private String resolutionType;
        private String resolutionNotes;
        private String resolvedBy;
        private Instant resolvedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSatisfactionDTO {
        private Integer rating;
        private String feedback;
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        if (ticket == null) return null;

        TicketResponseBuilder builder = TicketResponse.builder()
                .ticketId(ticket.getTicketId())
                .ticketNumber(ticket.getTicketNumber())
                .createdBy(ticket.getCreatedBy())
                .createdByName(ticket.getCreatedByName())
                .category(ticket.getCategory())
                .subcategory(ticket.getSubcategory())
                .priority(ticket.getPriority() != null ? ticket.getPriority().name() : null)
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .assignedTo(ticket.getAssignedTo())
                .assignedAt(ticket.getAssignedAt())
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .createdAt(ticket.getCreatedAt())
                .closedAt(ticket.getClosedAt());

        if (ticket.getRelatedEntities() != null) {
            builder.relatedEntities(RelatedEntitiesDTO.builder()
                    .orderId(ticket.getRelatedEntities().getOrderId())
                    .vendorId(ticket.getRelatedEntities().getVendorId())
                    .paymentId(ticket.getRelatedEntities().getPaymentId())
                    .build());
        }

        if (ticket.getSla() != null) {
            builder.sla(SlaDTO.builder()
                    .firstResponseDue(ticket.getSla().getFirstResponseDue())
                    .resolutionDue(ticket.getSla().getResolutionDue())
                    .firstResponseAt(ticket.getSla().getFirstResponseAt())
                    .resolvedAt(ticket.getSla().getResolvedAt())
                    .slaBreached(ticket.getSla().getSlaBreached())
                    .build());
        }

        if (ticket.getResolution() != null) {
            builder.resolution(ResolutionDTO.builder()
                    .resolutionType(ticket.getResolution().getResolutionType())
                    .resolutionNotes(ticket.getResolution().getResolutionNotes())
                    .resolvedBy(ticket.getResolution().getResolvedBy())
                    .resolvedAt(ticket.getResolution().getResolvedAt())
                    .build());
        }

        if (ticket.getCustomerSatisfaction() != null) {
            builder.customerSatisfaction(CustomerSatisfactionDTO.builder()
                    .rating(ticket.getCustomerSatisfaction().getRating())
                    .feedback(ticket.getCustomerSatisfaction().getFeedback())
                    .build());
        }

        return builder.build();
    }
}

