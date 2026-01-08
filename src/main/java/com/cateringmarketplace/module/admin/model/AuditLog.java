package com.cateringmarketplace.module.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * Audit log entity for tracking all important actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    @Field("log_id")
    private String logId;

    @Indexed
    @Field("entity_type")
    private String entityType;

    @Indexed
    @Field("entity_id")
    private String entityId;

    private String action;

    @Field("performed_by")
    private String performedBy;

    @Field("performed_by_type")
    private String performedByType;

    private Map<String, Object> changes;

    private AuditMetadata metadata;

    @Indexed
    private Instant timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditMetadata {
        @Field("ip_address")
        private String ipAddress;

        @Field("user_agent")
        private String userAgent;

        private String reason;
    }

    /**
     * Creates an audit log entry.
     */
    public static AuditLog create(String entityType, String entityId, String action,
                                   String performedBy, String performedByType,
                                   Map<String, Object> changes, AuditMetadata metadata) {
        return AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .performedByType(performedByType)
                .changes(changes)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }
}

