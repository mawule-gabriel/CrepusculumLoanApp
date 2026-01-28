package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import com.crepusculum.loanapp.travel_loan_manager.repository.LoanRepository;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void recordPayment_Success() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(new BigDecimal("1000"));
        BigDecimal amountPaid = new BigDecimal("200");
        String note = "Test note";
        String recorder = "ADMIN";

        when(paymentRepository.getTotalPaidByLoanId(1L)).thenReturn(new BigDecimal("200"));

        paymentService.recordPayment(loan, amountPaid, note, recorder);

        verify(paymentRepository).save(any(Payment.class));
        verify(loanRepository).save(loan);
        assert (loan.getTotalPaid().equals(new BigDecimal("200")));
        assert (loan.getBalance().equals(new BigDecimal("800")));
    }
}
