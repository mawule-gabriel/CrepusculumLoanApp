package com.crepusculum.loanapp.travel_loan_manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BorrowerDetailResponse(
        Long borrowerId,
        String fullName,
        String phoneNumber,
        String ghanaCardNumber,
        String homeAddressGhana,
        String destinationAddress,
        String profilePictureUrl,

        BigDecimal loanAmount,
        BigDecimal monthlyPayment,
        BigDecimal totalPaid,
        BigDecimal balance,
        LocalDate startDate,
        LocalDate endDate,
        int monthsDuration,

        String guarantorName,
        String guarantorPhone,
        String guarantorRelationship,

        List<PaymentResponse> payments
) {
    public record PaymentResponse(
            BigDecimal amount,
            LocalDate date,
            String recordedBy,
            String note
    ) {}
}
