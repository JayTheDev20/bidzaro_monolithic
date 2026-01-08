package com.cateringmarketplace.common.util;

import com.cateringmarketplace.common.constant.AppConstants;
import com.cateringmarketplace.common.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    @Value("${jwt.issuer:catering-platform}")
    private String issuer;

    /**
     * Generates an access token for the user.
     */
    public String generateAccessToken(String userId, String email, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConstants.JWT_CLAIM_USER_ID, userId);
        claims.put(AppConstants.JWT_CLAIM_EMAIL, email);
        claims.put(AppConstants.JWT_CLAIM_USER_TYPE, userType);

        return createToken(claims, email, accessTokenExpiration);
    }

    /**
     * Generates an access token with additional claims.
     */
    public String generateAccessToken(String userId, String email, String userType, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put(AppConstants.JWT_CLAIM_USER_ID, userId);
        claims.put(AppConstants.JWT_CLAIM_EMAIL, email);
        claims.put(AppConstants.JWT_CLAIM_USER_TYPE, userType);

        return createToken(claims, email, accessTokenExpiration);
    }

    /**
     * Generates a refresh token.
     */
    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConstants.JWT_CLAIM_USER_ID, userId);
        claims.put("tokenId", UUID.randomUUID().toString());

        return createToken(claims, email, refreshTokenExpiration);
    }

    /**
     * Creates a JWT token with the specified claims.
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts username (email) from token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts user ID from token.
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(AppConstants.JWT_CLAIM_USER_ID, String.class));
    }

    /**
     * Extracts user type from token.
     */
    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get(AppConstants.JWT_CLAIM_USER_TYPE, String.class));
    }

    /**
     * Extracts expiration date from token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new UnauthorizedException("Token has expired");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new UnauthorizedException("Invalid token format");
        } catch (JwtException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            throw new UnauthorizedException("Token validation failed");
        }
    }

    /**
     * Checks if token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (UnauthorizedException e) {
            return true;
        }
    }

    /**
     * Validates the token against user details.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates the token structure and signature.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the signing key from the secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets remaining time until token expires in milliseconds.
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Gets access token expiration time in milliseconds.
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Gets refresh token expiration time in milliseconds.
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}

