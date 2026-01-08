package com.cateringmarketplace.module.referral.repository;

import com.cateringmarketplace.module.referral.model.ReferralCode;
import com.cateringmarketplace.module.referral.model.ReferralCode.ReferralStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ReferralCode entity.
 */
@Repository
public interface ReferralCodeRepository extends MongoRepository<ReferralCode, String> {

    Optional<ReferralCode> findByCodeId(String codeId);

    Optional<ReferralCode> findByUserId(String userId);

    Optional<ReferralCode> findByCodeIgnoreCase(String code);

    Optional<ReferralCode> findByCodeIgnoreCaseAndStatus(String code, ReferralStatus status);

    boolean existsByUserId(String userId);

    boolean existsByCodeIgnoreCase(String code);
}

