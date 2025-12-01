package com.crepusculum.loanapp.travel_loan_manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BorrowerSummaryResponse(
        Long id,
        String fullName,
        String phoneNumber,
        String ghanaCardNumber,
        String profilePictureUrl,
        BigDecimal loanAmount,
        BigDecimal monthlyPayment,
        BigDecimal totalPaid,
        BigDecimal balance,
        LocalDate startDate,
        LocalDate endDate,
        int monthsPaid,
        int totalMonths,
        String status
) {
}
