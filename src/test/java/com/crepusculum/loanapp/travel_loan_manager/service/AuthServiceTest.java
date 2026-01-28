package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.LoginRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RefreshTokenRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.JwtResponse;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.RefreshTokenResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.RefreshToken;
import com.crepusculum.loanapp.travel_loan_manager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private static final long ACCESS_TOKEN_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("233200000000", "password123");
        Authentication authentication = mock(Authentication.class);
        Borrower userDetails = mock(Borrower.class);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token-123");
        when(userDetails.getUsername()).thenReturn("233200000000");
        when(userDetails.getFullName()).thenReturn("John Doe");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_BORROWER")))
                .when(userDetails).getAuthorities();
        when(userDetails.getPasswordResetRequired()).thenReturn(false);
        when(refreshTokenService.createRefreshToken("233200000000")).thenReturn(refreshToken);

        JwtResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access-token-123", response.accessToken());
        assertEquals("refresh-token-123", response.refreshToken());
        assertEquals("ROLE_BORROWER", response.role());
        assertEquals("John Doe", response.name());
        assertEquals(ACCESS_TOKEN_EXPIRATION / 1000, response.expiresIn());
        assertFalse(response.passwordResetRequired());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("old-refresh-token");
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new-refresh-token");
        Borrower borrower = new Borrower();
        borrower.setPhoneNumber("233200000000");
        newRefreshToken.setBorrower(borrower);

        when(refreshTokenService.rotateRefreshToken("old-refresh-token")).thenReturn(newRefreshToken);
        when(jwtService.generateAccessToken("233200000000")).thenReturn("new-access-token");

        RefreshTokenResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        verify(refreshTokenService).rotateRefreshToken("old-refresh-token");
    }

    @Test
    void logout_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("token-to-delete");
        RefreshToken refreshToken = new RefreshToken();
        Borrower borrower = new Borrower();
        refreshToken.setBorrower(borrower);

        when(refreshTokenService.findByToken("token-to-delete")).thenReturn(Optional.of(refreshToken));

        authService.logout(request);

        verify(refreshTokenService).deleteByBorrower(borrower);
    }

    @Test
    void logout_TokenNotFound_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(refreshTokenService.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.logout(request));
    }
}
