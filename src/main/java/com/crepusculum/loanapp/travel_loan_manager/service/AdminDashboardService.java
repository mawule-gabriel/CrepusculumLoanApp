package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final BorrowerRepository borrowerRepository;
    private final PaymentRepository paymentRepository;

    public List<BorrowerSummaryResponse> getAllBorrowerSummaries() {
        return borrowerRepository.findAll().stream()
                .filter(b -> b.getLoan() != null)
                .map(this::mapToSummary)
                .toList();
    }

    private BorrowerSummaryResponse mapToSummary(Borrower b) {
        Loan l = b.getLoan();
        int monthsPaid = paymentRepository.findByLoanIdOrderByPaymentDateDesc(l.getId()).size();
        int expectedMonths = LocalDate.now().getMonthValue() - l.getStartDate().getMonthValue()
                + (LocalDate.now().getYear() - l.getStartDate().getYear()) * 12;
        String status = monthsPaid >= expectedMonths ? "On Track" : "Delayed";

        return new BorrowerSummaryResponse(
                b.getId(),
                b.getFullName(),
                b.getPhoneNumber(),
                b.getGhanaCardNumber(),
                b.getProfilePicturePath(),
                l.getAmount(),
                l.getMonthlyPayment(),
                l.getTotalPaid(),
                l.getBalance(),
                l.getStartDate(),
                l.getEndDate(),
                monthsPaid,
                l.getMonthsDuration(),
                status
        );
    }
}
