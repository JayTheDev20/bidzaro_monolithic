package com.cateringmarketplace.module.auth.repository;

import com.cateringmarketplace.module.auth.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for refresh token operations.
 */
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    /**
     * Find by token ID.
     */
    Optional<RefreshToken> findByTokenId(String tokenId);

    /**
     * Find by token hash.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find valid token by hash.
     */
    @Query("{ 'token_hash': ?0, 'revoked': false, 'expires_at': { $gt: ?1 } }")
    Optional<RefreshToken> findValidTokenByHash(String tokenHash, Instant now);

    /**
     * Find all tokens for a user.
     */
    List<RefreshToken> findByUserId(String userId);

    /**
     * Find active tokens for a user.
     */
    @Query("{ 'user_id': ?0, 'revoked': false, 'expires_at': { $gt: ?1 } }")
    List<RefreshToken> findActiveTokensByUserId(String userId, Instant now);

    /**
     * Find expired tokens.
     */
    @Query("{ 'expires_at': { $lt: ?0 } }")
    List<RefreshToken> findExpiredTokens(Instant now);

    /**
     * Revoke all tokens for a user.
     */
    @Query("{ 'user_id': ?0, 'revoked': false }")
    List<RefreshToken> findTokensToRevokeByUserId(String userId);

    /**
     * Delete expired tokens.
     */
    void deleteByExpiresAtBefore(Instant date);

    /**
     * Delete tokens by user ID.
     */
    void deleteByUserId(String userId);

    /**
     * Count active tokens for a user.
     */
    @Query(value = "{ 'user_id': ?0, 'revoked': false, 'expires_at': { $gt: ?1 } }", count = true)
    long countActiveTokensByUserId(String userId, Instant now);

    /**
     * Check if valid token exists.
     */
    @Query(value = "{ 'token_hash': ?0, 'revoked': false, 'expires_at': { $gt: ?1 } }", exists = true)
    boolean existsValidToken(String tokenHash, Instant now);
}
