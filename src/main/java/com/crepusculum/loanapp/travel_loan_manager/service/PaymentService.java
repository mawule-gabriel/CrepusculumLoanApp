package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import com.crepusculum.loanapp.travel_loan_manager.repository.LoanRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public void recordPayment(Loan loan, BigDecimal amount, String note, String recordedBy) {
        Payment payment = Payment.builder()
                .amountPaid(amount)
                .paymentDate(LocalDate.now())
                .recordedBy(recordedBy)
                .note(note)
                .loan(loan)
                .build();

        paymentRepository.save(payment);

        BigDecimal newTotalPaid = paymentRepository.getTotalPaidByLoanId(loan.getId());
        loan.setTotalPaid(newTotalPaid);
        loan.setBalance(loan.getAmount().subtract(newTotalPaid));
        loanRepository.save(loan);
    }
}