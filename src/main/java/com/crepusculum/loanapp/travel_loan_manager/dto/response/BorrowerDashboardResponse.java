package com.crepusculum.loanapp.travel_loan_manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BorrowerDashboardResponse(
        String fullName,
        String phoneNumber,
        String profilePictureUrl,
        String homeAddressGhana,
        String destinationAddress,

        BigDecimal loanAmount,
        BigDecimal monthlyPayment,
        BigDecimal totalPaid,
        BigDecimal balance,
        LocalDate startDate,
        LocalDate nextDueDate,
        LocalDate endDate,
        int totalMonths,
        int monthsPaid,
        int monthsRemaining,
        String status,

        String guarantorName,
        String guarantorPhone,

        List<PaymentHistory> paymentHistory
) {
    public record PaymentHistory(
            BigDecimal amount,
            LocalDate date,
            String recordedBy
    ) {}
}