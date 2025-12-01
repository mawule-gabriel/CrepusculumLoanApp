package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public Loan createLoan(Borrower borrower,
                           BigDecimal amount,
                           Integer monthsDuration) {

        if (monthsDuration == null || monthsDuration < 11) {
            monthsDuration = 11;
        }

        BigDecimal monthlyPayment = amount
                .divide(BigDecimal.valueOf(monthsDuration), 2, RoundingMode.HALF_UP);

        Loan loan = Loan.builder()
                .amount(amount)
                .monthlyPayment(monthlyPayment)
                .totalPaid(BigDecimal.ZERO)
                .balance(amount)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(monthsDuration))
                .monthsDuration(monthsDuration)
                .borrower(borrower)
                .build();

        return loanRepository.save(loan);
    }
}