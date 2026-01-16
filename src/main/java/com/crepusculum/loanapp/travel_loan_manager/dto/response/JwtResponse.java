package com.crepusculum.loanapp.travel_loan_manager.dto.response;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String role,
        String name,
        long expiresIn,
        boolean passwordResetRequired
) {
}
