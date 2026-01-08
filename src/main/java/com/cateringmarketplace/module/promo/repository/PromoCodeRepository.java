package com.cateringmarketplace.module.promo.repository;

import com.cateringmarketplace.module.promo.model.PromoCode;
import com.cateringmarketplace.module.promo.model.PromoCode.PromoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PromoCode entity.
 */
@Repository
public interface PromoCodeRepository extends MongoRepository<PromoCode, String> {

    Optional<PromoCode> findByPromoCodeId(String promoCodeId);

    Optional<PromoCode> findByCodeIgnoreCase(String code);

    Optional<PromoCode> findByCodeIgnoreCaseAndStatus(String code, PromoStatus status);

    Page<PromoCode> findByStatus(PromoStatus status, Pageable pageable);

    Page<PromoCode> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

    @Query("{'status': 'ACTIVE', 'valid_from': {'$lte': ?0}, 'valid_to': {'$gte': ?0}}")
    List<PromoCode> findActivePromos(Instant now);

    @Query("{'status': 'ACTIVE', 'valid_to': {'$lt': ?0}}")
    List<PromoCode> findExpiredPromos(Instant now);
}

