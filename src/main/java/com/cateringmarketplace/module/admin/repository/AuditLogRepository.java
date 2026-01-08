package com.cateringmarketplace.module.admin.repository;

import com.cateringmarketplace.module.admin.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for AuditLog entity.
 */
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    Page<AuditLog> findByEntityTypeOrderByTimestampDesc(String entityType, Pageable pageable);

    Page<AuditLog> findByEntityIdOrderByTimestampDesc(String entityId, Pageable pageable);

    Page<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy, Pageable pageable);

    @Query("{'timestamp': {'$gte': ?0, '$lte': ?1}}")
    Page<AuditLog> findByDateRange(Instant start, Instant end, Pageable pageable);

    @Query("{'entity_type': ?0, 'action': ?1}")
    Page<AuditLog> findByEntityTypeAndAction(String entityType, String action, Pageable pageable);

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}

