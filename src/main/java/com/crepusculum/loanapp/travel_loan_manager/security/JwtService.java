package com.crepusculum.loanapp.travel_loan_manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    /**
     * Validates the JWT secret key on application startup.
     * Ensures the secret is properly Base64-encoded and meets HS256 requirements.
     */
    @PostConstruct
    public void validateSecretKey() {
        try {
            logger.info("Raw secret from config: {}", secretKey);
            SecretKey key = getSignInKey();
            int keyLength = key.getEncoded().length * 8;
            logger.info("Decoded key length: {} bits", keyLength);

            if (keyLength < 256) {
                throw new IllegalStateException(
                        String.format("JWT secret key is too short (%d bits)...", keyLength)
                );
            }

            logger.info("JWT Service initialized successfully with {}-bit secret key", keyLength);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode Base64 secret", e);
            throw new IllegalStateException(
                    "JWT secret key is not properly Base64-encoded. Please check your configuration.", e
            );
        }
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username from the token's subject claim
     * @throws JwtException if the token is invalid or expired
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token using a claims resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract the desired claim
     * @param <T>            the type of the claim
     * @return the extracted claim value
     * @throws JwtException if the token is invalid or expired
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates an access token with only essential claims: sub, iat, exp.
     * No roles or permissions are included in the JWT for security reasons.
     *
     * @param username the username to include in the token
     * @return the generated JWT token
     */
    public String generateAccessToken(String username) {
        try {
            return Jwts
                    .builder()
                    .subject(username)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating access token for user: {}", username, e);
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    /**
     * Generates an access token from UserDetails.
     *
     * @param userDetails the user details containing the username
     * @return the generated JWT token
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails.getUsername());
    }

    /**
     * Validates the access token against the user details.
     * Checks both the username match and token expiration.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid and belongs to the user, false otherwise
     */
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates the access token structure and expiration without checking against specific user details.
     *
     * @param token the JWT token to validate
     * @return true if the token is structurally valid and not expired, false otherwise
     */
    public boolean validateAccessToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation", e);
            return false;
        }
    }

    /**
     * Checks if the token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the token.
     * This method verifies the token signature using the configured secret key.
     *
     * @param token the JWT token
     * @return all claims from the token
     * @throws JwtException if the token is invalid, expired, or signature verification fails
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gets the signing key for HS256 algorithm.
     * Decodes the Base64-encoded secret and creates an HMAC-SHA key.
     *
     * @return the secret key for signing/verifying JWTs
     * @throws IllegalArgumentException if the secret is not properly Base64-encoded
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}