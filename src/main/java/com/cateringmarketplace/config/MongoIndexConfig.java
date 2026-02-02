package com.cateringmarketplace.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * MongoDB index configuration for ensuring required indexes exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        createVendorGeoSpatialIndex();
    }

    /**
     * Creates 2dsphere index on vendor business_address.gps_coordinates field.
     * This is required for $nearSphere queries.
     */
    private void createVendorGeoSpatialIndex() {
        try {
            MongoCollection<Document> vendorCollection = mongoTemplate.getCollection("vendors");

            // Check if index already exists
            boolean indexExists = false;
            for (Document index : vendorCollection.listIndexes()) {
                if (index.get("name") != null &&
                    index.get("name").toString().contains("business_address.gps_coordinates")) {
                    indexExists = true;
                    break;
                }
            }

            if (!indexExists) {
                log.info("Creating 2dsphere index on vendors.business_address.gps_coordinates");
                vendorCollection.createIndex(
                    Indexes.geo2dsphere("business_address.gps_coordinates"),
                    new IndexOptions().name("business_address_gps_coordinates_2dsphere")
                );
                log.info("Successfully created geospatial index on vendors collection");
            } else {
                log.info("Geospatial index on vendors.business_address.gps_coordinates already exists");
            }
        } catch (Exception e) {
            log.error("Failed to create geospatial index on vendors collection", e);
            // Don't throw exception - let application start even if index creation fails
            // Index can be created manually in MongoDB if needed
        }
    }
}
