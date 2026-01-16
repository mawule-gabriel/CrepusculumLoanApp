package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.LoginRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RefreshTokenRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.JwtResponse;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.RefreshTokenResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.RefreshToken;
import com.crepusculum.loanapp.travel_loan_manager.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param request the login request containing phone number and password
     * @return JwtResponse containing access token, refresh token, role, and user info
     */
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.phoneNumber(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        String role = extractUserRole(userDetails);
        String name = extractUserName(userDetails);
        boolean passwordResetRequired = extractPasswordResetRequired(userDetails);

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                role,
                name,
                accessTokenExpiration / 1000,
                passwordResetRequired
        );
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param request the refresh token request
     * @return RefreshTokenResponse containing new access token and refresh token
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.refreshToken();

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(requestRefreshToken);

        String accessToken = jwtService.generateAccessToken(newRefreshToken.getBorrower().getUsername());

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken.getToken(),
                accessTokenExpiration / 1000
        );
    }

    /**
     * Logs out a user by invalidating their refresh token.
     *
     * @param request the refresh token request
     */
    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.deleteByBorrower(refreshToken.getBorrower());
    }

    /**
     * Extracts the role from user details.
     *
     * @param userDetails the authenticated user details
     * @return the user's role
     */
    private String extractUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
    }

    /**
     * Extracts the full name from user details if available.
     *
     * @param userDetails the authenticated user details
     * @return the user's full name or "User" as default
     */
    private String extractUserName(UserDetails userDetails) {
        if (userDetails instanceof Borrower borrower) {
            return borrower.getFullName();
        }
        return "User";
    }

    private boolean extractPasswordResetRequired(UserDetails userDetails) {
        if (userDetails instanceof Borrower borrower) {
            return Boolean.TRUE.equals(borrower.getPasswordResetRequired());
        }
        return false;
    }
}