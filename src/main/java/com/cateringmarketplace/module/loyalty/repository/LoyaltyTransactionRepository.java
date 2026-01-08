package com.cateringmarketplace.module.loyalty.repository;

import com.cateringmarketplace.module.loyalty.model.LoyaltyTransaction;
import com.cateringmarketplace.module.loyalty.model.LoyaltyTransaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for LoyaltyTransaction entity.
 */
@Repository
public interface LoyaltyTransactionRepository extends MongoRepository<LoyaltyTransaction, String> {

    Optional<LoyaltyTransaction> findByTransactionId(String transactionId);

    Page<LoyaltyTransaction> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<LoyaltyTransaction> findByUserIdAndType(String userId, TransactionType type);

    @Query("{'user_id': ?0, 'type': 'EARN'}")
    List<LoyaltyTransaction> findEarnTransactionsByUserId(String userId);

    @Query("{'user_id': ?0, 'type': 'REDEEM'}")
    List<LoyaltyTransaction> findRedeemTransactionsByUserId(String userId);

    Optional<LoyaltyTransaction> findByOrderIdAndType(String orderId, TransactionType type);

    @Query("{'type': 'EARN', 'expires_at': {'$lt': ?0, '$ne': null}}")
    List<LoyaltyTransaction> findExpiredPoints(Instant now);

    @Query(value = "{'user_id': ?0}", count = true)
    long countByUserId(String userId);
}

