package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.AdminResetPasswordResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.PasswordResetToken;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final BorrowerRepository borrowerRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiry-minutes:60}")
    private int tokenExpiryMinutes;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public boolean initiatePasswordReset(String phoneNumber) {
        Borrower borrower = borrowerRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (borrower == null) {
            return true;
        }

        if (borrower.getEmail() == null || borrower.getEmail().isBlank()) {
            throw new IllegalArgumentException("No email associated with this account. Please contact an Admin to reset your password.");
        }

        tokenRepository.deleteByBorrower(borrower);

        String token = generateSecureToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .borrower(borrower)
                .expiresAt(Instant.now().plus(tokenExpiryMinutes, ChronoUnit.MINUTES))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(borrower.getEmail(), token);

        return true;
    }

    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        Borrower borrower = resetToken.getBorrower();
        borrower.setPassword(passwordEncoder.encode(newPassword));
        borrower.setPasswordResetRequired(false);
        borrowerRepository.save(borrower);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    @Transactional
    public AdminResetPasswordResponse adminResetPassword(Long borrowerId, String newPassword) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found"));

        String passwordToSet = newPassword;
        if (passwordToSet == null || passwordToSet.isBlank()) {
            passwordToSet = generateSecureToken().substring(0, 8);
        }

        borrower.setPassword(passwordEncoder.encode(passwordToSet));
        borrower.setPasswordResetRequired(true);
        borrowerRepository.save(borrower);

        tokenRepository.deleteByBorrower(borrower);

        return new AdminResetPasswordResponse(
                "Password reset successfully. User will be required to change it on next login.",
                passwordToSet
        );
    }

    @Transactional
    public void changePassword(Long borrowerId, String currentPassword, String newPassword) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found"));

        if (!passwordEncoder.matches(currentPassword, borrower.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        borrower.setPassword(passwordEncoder.encode(newPassword));
        borrower.setPasswordResetRequired(false);
        borrowerRepository.save(borrower);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
