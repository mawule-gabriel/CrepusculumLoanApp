package com.crepusculum.loanapp.travel_loan_manager.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
