package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDashboardResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowerDashboardService {

    private final PaymentRepository paymentRepository;

    public BorrowerDashboardResponse getDashboard(Borrower borrower) {
        Loan loan = borrower.getLoan();
        if (loan == null) {
            throw new IllegalStateException("No loan found");
        }

        List<Payment> payments = paymentRepository.findByLoanIdOrderByPaymentDateDesc(loan.getId());
        int monthsPaid = payments.size();

        LocalDate nextDueDate = loan.getStartDate().plusMonths(monthsPaid);
        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), nextDueDate);
        int monthsRemaining = loan.getMonthsDuration() - monthsPaid;

        String status;
        if (monthsRemaining <= 0) {
            status = "Completed";
        } else if (daysUntilDue < 0) {
            status = "Delayed";
        } else {
            status = "On Track";
        }

        var paymentHistory = payments.stream()
                .map(p -> new BorrowerDashboardResponse.PaymentHistory(
                        p.getAmountPaid(),
                        p.getPaymentDate(),
                        p.getRecordedBy()
                ))
                .toList();

        return new BorrowerDashboardResponse(
                borrower.getFullName(),
                borrower.getPhoneNumber(),
                borrower.getProfilePicturePath(),
                borrower.getHomeAddressGhana(),
                borrower.getDestinationAddress(),
                loan.getAmount(),
                loan.getMonthlyPayment(),
                loan.getTotalPaid(),
                loan.getBalance(),
                loan.getStartDate(),
                nextDueDate,
                loan.getEndDate(),
                loan.getMonthsDuration(),
                monthsPaid,
                monthsRemaining,
                status,
                borrower.getGuarantor() != null ? borrower.getGuarantor().getFullName() : "N/A",
                borrower.getGuarantor() != null ? borrower.getGuarantor().getPhoneNumber() : "N/A",
                paymentHistory
        );
    }
}