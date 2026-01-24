package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.RefreshToken;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private static final long REFRESH_TOKEN_EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void createRefreshToken_Success() {
        String phoneNumber = "233200000000";
        Borrower borrower = new Borrower();
        borrower.setPhoneNumber(phoneNumber);

        when(borrowerRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(borrower));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(phoneNumber);

        assertNotNull(result);
        assertEquals(borrower, result.getBorrower());
        assertNotNull(result.getToken());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ValidToken_NoException() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(10000));
        token.setRevoked(false);

        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(token));
    }

    @Test
    void verifyExpiration_ExpiredToken_ThrowsException() {

        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusMillis(10000));
        token.setRevoked(false);

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void verifyExpiration_RevokedToken_ThrowsException() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(10000));
        token.setRevoked(true);


        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void rotateRefreshToken_Success() {
        String oldTokenStr = "old-token";
        RefreshToken oldToken = new RefreshToken();
        oldToken.setToken(oldTokenStr);
        oldToken.setExpiryDate(Instant.now().plusMillis(10000));
        oldToken.setRevoked(false);

        Borrower borrower = new Borrower();
        borrower.setPhoneNumber("233200000000");
        oldToken.setBorrower(borrower);

        when(refreshTokenRepository.findByToken(oldTokenStr)).thenReturn(Optional.of(oldToken));
        when(borrowerRepository.findByPhoneNumber("233200000000")).thenReturn(Optional.of(borrower));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.rotateRefreshToken(oldTokenStr);

        assertNotNull(result);
        assertTrue(oldToken.isRevoked());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }
}
