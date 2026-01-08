package com.cateringmarketplace.module.auth.repository;

import com.cateringmarketplace.module.auth.model.OTPVerification;
import com.cateringmarketplace.module.auth.model.OTPVerification.VerificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OTP verification operations.
 */
@Repository
public interface OTPVerificationRepository extends MongoRepository<OTPVerification, String> {

    /**
     * Find by verification ID.
     */
    Optional<OTPVerification> findByVerificationId(String verificationId);

    /**
     * Find by identifier and verification type.
     */
    Optional<OTPVerification> findByIdentifierAndVerificationType(
            String identifier, VerificationType verificationType);

    /**
     * Find latest valid OTP for identifier and type.
     */
    @Query("{ 'identifier': ?0, 'verification_type': ?1, 'verified': false, 'expires_at': { $gt: ?2 } }")
    Optional<OTPVerification> findLatestValidOTP(String identifier, VerificationType type, Instant now);

    /**
     * Find by user ID and verification type.
     */
    Optional<OTPVerification> findByUserIdAndVerificationType(
            String userId, VerificationType verificationType);

    /**
     * Find all OTPs for a user.
     */
    List<OTPVerification> findByUserId(String userId);

    /**
     * Find expired OTPs.
     */
    @Query("{ 'expires_at': { $lt: ?0 } }")
    List<OTPVerification> findExpiredOTPs(Instant now);

    /**
     * Delete expired OTPs.
     */
    void deleteByExpiresAtBefore(Instant date);

    /**
     * Delete OTPs by user ID.
     */
    void deleteByUserId(String userId);

    /**
     * Delete OTPs by identifier and type.
     */
    void deleteByIdentifierAndVerificationType(String identifier, VerificationType type);

    /**
     * Count OTPs sent to identifier in last N minutes.
     */
    @Query(value = "{ 'identifier': ?0, 'created_at': { $gt: ?1 } }", count = true)
    long countRecentOTPsByIdentifier(String identifier, Instant since);
}
