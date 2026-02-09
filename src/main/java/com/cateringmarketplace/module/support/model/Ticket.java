package com.cateringmarketplace.module.support.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Support Ticket entity for customer support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "support_tickets")
public class Ticket {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("ticket_id")
    private String ticketId;

    @Indexed(unique = true)
    @Field("ticket_number")
    private String ticketNumber;

    @Indexed
    @Field("created_by")
    private String createdBy;

    @Field("created_by_type")
    private String createdByType;

    @Field("created_by_name")
    private String createdByName;

    @Field("created_by_email")
    private String createdByEmail;

    @Field("created_by_phone")
    private String createdByPhone;

    @Indexed
    private String category;

    private String subcategory;

    @Indexed
    private TicketPriority priority;

    private String subject;

    private String description;

    @Field("related_entities")
    private RelatedEntities relatedEntities;

    @Builder.Default
    private List<TicketAttachment> attachments = new ArrayList<>();

    @Field("assigned_to")
    private String assignedTo;

    @Field("assigned_at")
    private Instant assignedAt;
    
    @Field("conversation_id")
    private String conversationId; // Linked Chat Conversation

    @Indexed
    private TicketStatus status;

    private SLA sla;

    private Resolution resolution;

    @Field("customer_satisfaction")
    private CustomerSatisfaction customerSatisfaction;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("closed_at")
    private Instant closedAt;

    // Enums
    public enum TicketPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public enum TicketStatus {
        OPEN,
        ASSIGNED,
        IN_PROGRESS,
        WAITING_FOR_CUSTOMER,
        RESOLVED,
        CLOSED,
        ESCALATED
    }

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedEntities {
        @Field("order_id")
        private String orderId;
        @Field("vendor_id")
        private String vendorId;
        @Field("payment_id")
        private String paymentId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketAttachment {
        @Field("file_name")
        private String fileName;
        @Field("file_url")
        private String fileUrl;
        @Field("file_type")
        private String fileType;
        @Field("uploaded_at")
        private Instant uploadedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SLA {
        @Field("first_response_due")
        private Instant firstResponseDue;
        @Field("resolution_due")
        private Instant resolutionDue;
        @Field("first_response_at")
        private Instant firstResponseAt;
        @Field("resolved_at")
        private Instant resolvedAt;
        @Field("sla_breached")
        @Builder.Default
        private Boolean slaBreached = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resolution {
        @Field("resolution_type")
        private String resolutionType;
        @Field("resolution_notes")
        private String resolutionNotes;
        @Field("resolved_by")
        private String resolvedBy;
        @Field("resolved_at")
        private Instant resolvedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSatisfaction {
        private Integer rating;
        private String feedback;
    }

    /**
     * Generates next ticket number.
     */
    public static String generateTicketNumber(long sequence) {
        return String.format("TKT-%06d", sequence);
    }

    /**
     * Checks if ticket is open.
     */
    public boolean isOpen() {
        return status != TicketStatus.RESOLVED && status != TicketStatus.CLOSED;
    }

    /**
     * Checks if SLA is breached.
     */
    public boolean isSlaBreached() {
        if (sla == null) return false;
        Instant now = Instant.now();

        if (sla.getFirstResponseAt() == null && sla.getFirstResponseDue() != null &&
            now.isAfter(sla.getFirstResponseDue())) {
            return true;
        }

        if (status != TicketStatus.RESOLVED && status != TicketStatus.CLOSED &&
            sla.getResolutionDue() != null && now.isAfter(sla.getResolutionDue())) {
            return true;
        }

        return Boolean.TRUE.equals(sla.getSlaBreached());
    }
}
