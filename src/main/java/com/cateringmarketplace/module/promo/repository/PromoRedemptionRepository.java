package com.cateringmarketplace.module.promo.repository;

import com.cateringmarketplace.module.promo.model.PromoRedemption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PromoRedemption entity.
 */
@Repository
public interface PromoRedemptionRepository extends MongoRepository<PromoRedemption, String> {

    Optional<PromoRedemption> findByRedemptionId(String redemptionId);

    List<PromoRedemption> findByUserId(String userId);

    Page<PromoRedemption> findByUserIdOrderByRedeemedAtDesc(String userId, Pageable pageable);

    List<PromoRedemption> findByPromoCodeId(String promoCodeId);

    long countByPromoCodeId(String promoCodeId);

    long countByUserIdAndPromoCodeId(String userId, String promoCodeId);

    boolean existsByUserIdAndPromoCodeId(String userId, String promoCodeId);

    Optional<PromoRedemption> findByOrderId(String orderId);
}

