package com.cateringmarketplace.module.admin.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cateringmarketplace.module.admin.model.PlatformConfig;

/**
 * Repository for PlatformConfig entity.
 */
@Repository
public interface PlatformConfigRepository extends MongoRepository<PlatformConfig, String> {

    Optional<PlatformConfig> findByConfigId(String configId);

    Optional<PlatformConfig> findByCountry(String country);

    boolean existsByCountry(String country);
}
