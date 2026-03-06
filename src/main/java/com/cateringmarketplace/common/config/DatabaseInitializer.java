package com.cateringmarketplace.common.config;

import com.cateringmarketplace.module.vendor.model.Vendor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        log.info("Initializing MongoDB indexes...");

        try {
            IndexOperations vendorIndexOps = mongoTemplate.indexOps(Vendor.class);

            // Note: 2dsphere index is managed by MongoIndexConfig — not duplicated here
            // Ensure other important indexes exist
            vendorIndexOps.ensureIndex(new Index().on("vendor_id", Sort.Direction.ASC).unique());
            vendorIndexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
            vendorIndexOps.ensureIndex(new Index().on("status", Sort.Direction.ASC));
            vendorIndexOps.ensureIndex(new Index().on("approval_status", Sort.Direction.ASC));

            log.info("MongoDB indexes initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize MongoDB indexes: {}", e.getMessage());
        }
    }
}
