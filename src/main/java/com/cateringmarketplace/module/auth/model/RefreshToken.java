package com.cateringmarketplace.module.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * Refresh token entity for JWT token refresh functionality.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("token_id")
    @Builder.Default
    private String tokenId = UUID.randomUUID().toString();

    @Indexed
    @Field("user_id")
    private String userId;

    /**
     * The refresh token value (hashed for security)
     */
    @Indexed(unique = true)
    @Field("token_hash")
    private String tokenHash;

    /**
     * Device information for the token
     */
    @Field("device_info")
    private String deviceInfo;

    /**
     * IP address from which token was issued
     */
    @Field("ip_address")
    private String ipAddress;

    /**
     * User agent string
     */
    @Field("user_agent")
    private String userAgent;

    /**
     * Whether the token has been revoked
     */
    @Builder.Default
    private Boolean revoked = false;

    /**
     * When the token was revoked
     */
    @Field("revoked_at")
    private Instant revokedAt;

    /**
     * Reason for revocation
     */
    @Field("revocation_reason")
    private String revocationReason;

    /**
     * Token that replaced this one (if refreshed)
     */
    @Field("replaced_by")
    private String replacedBy;

    /**
     * When the token expires
     */
    @Indexed
    @Field("expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    /**
     * Last used timestamp
     */
    @Field("last_used_at")
    private Instant lastUsedAt;

    /**
     * Checks if the token has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token is valid (not revoked and not expired).
     */
    public boolean isValid() {
        return !revoked && !isExpired();
    }

    /**
     * Revokes the token.
     */
    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revocationReason = reason;
    }

    /**
     * Updates the last used timestamp.
     */
    public void updateLastUsed() {
        this.lastUsedAt = Instant.now();
    }
}
