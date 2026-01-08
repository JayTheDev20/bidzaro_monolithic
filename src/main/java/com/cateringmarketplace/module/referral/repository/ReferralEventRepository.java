package com.cateringmarketplace.module.referral.repository;

import com.cateringmarketplace.module.referral.model.ReferralEvent;
import com.cateringmarketplace.module.referral.model.ReferralEvent.EventType;
import com.cateringmarketplace.module.referral.model.ReferralEvent.RewardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ReferralEvent entity.
 */
@Repository
public interface ReferralEventRepository extends MongoRepository<ReferralEvent, String> {

    Optional<ReferralEvent> findByEventId(String eventId);

    List<ReferralEvent> findByReferrerUserId(String referrerUserId);

    Page<ReferralEvent> findByReferrerUserIdOrderByCreatedAtDesc(String referrerUserId, Pageable pageable);

    Optional<ReferralEvent> findByReferredUserId(String referredUserId);

    Optional<ReferralEvent> findByReferrerUserIdAndReferredUserId(String referrerUserId, String referredUserId);

    @Query("{'referrer_user_id': ?0, 'reward_status': 'GRANTED'}")
    List<ReferralEvent> findGrantedReferralsByReferrer(String referrerUserId);

    @Query("{'referrer_user_id': ?0, 'reward_status': 'PENDING'}")
    List<ReferralEvent> findPendingReferralsByReferrer(String referrerUserId);

    long countByReferrerUserId(String referrerUserId);

    long countByReferrerUserIdAndRewardStatus(String referrerUserId, RewardStatus status);

    long countByReferrerUserIdAndEventType(String referrerUserId, EventType eventType);

    boolean existsByReferredUserId(String referredUserId);
}

