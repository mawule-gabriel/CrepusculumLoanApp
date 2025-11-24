package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.RefreshToken;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final BorrowerRepository borrowerRepository;

    /**
     * Creates a new refresh token for the given username.
     */
    public RefreshToken createRefreshToken(String username) {
        Borrower borrower = borrowerRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        RefreshToken refreshToken = RefreshToken.builder()
                .borrower(borrower)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token string.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies that the refresh token is not expired and not revoked.
     * Throws an exception if the token is invalid.
     */
    public void verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was revoked. Please login again.");
        }
        
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token has expired. Please login again.");
        }

    }

    /**
     * Rotates the refresh token by revoking the old one and creating a new one.
     * This implements refresh token rotation for enhanced security.
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken) {
        RefreshToken existingToken = findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        verifyExpiration(existingToken);

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        return createRefreshToken(existingToken.getBorrower().getUsername());
    }

    /**
     * Deletes all refresh tokens for a specific borrower (used for logout).
     */
    @Transactional
    public void deleteByBorrower(Borrower borrower) {
        refreshTokenRepository.deleteByBorrower(borrower);
    }

    /**
     * Deletes all expired refresh tokens (cleanup job).
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    /**
     * Deletes all revoked tokens (cleanup job).
     */
    @Transactional
    public void deleteRevokedTokens() {
        refreshTokenRepository.deleteRevokedTokens();
    }
}
