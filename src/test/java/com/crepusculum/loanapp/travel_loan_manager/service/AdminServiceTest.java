package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.request.RecordPaymentRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.request.RegisterBorrowerRequest;
import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerDetailResponse;
import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Loan;
import com.crepusculum.loanapp.travel_loan_manager.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private BorrowerService borrowerService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void registerBorrower_Success() {
        RegisterBorrowerRequest request = mock(RegisterBorrowerRequest.class);
        when(request.phoneNumber()).thenReturn("233201234567");

        Borrower borrower = new Borrower();
        borrower.setFullName("John Doe");
        borrower.setPhoneNumber("233201234567");
        Loan loan = new Loan();
        loan.setAmount(new BigDecimal("5000"));
        loan.setMonthsDuration(12);
        loan.setMonthlyPayment(new BigDecimal("500"));
        borrower.setLoan(loan);

        when(borrowerService.createBorrowerWithGuarantor(request)).thenReturn(borrower);

        String result = adminService.registerBorrower(request);

        assertNotNull(result);
        assertTrue(result.contains("John Doe"));
        verify(borrowerService).createBorrowerWithGuarantor(request);
    }

    @Test
    void recordPayment_Success() {

        RecordPaymentRequest request = new RecordPaymentRequest(1L, new BigDecimal("500"), "Monthly pay");
        Borrower borrower = new Borrower();
        Loan loan = new Loan();
        borrower.setLoan(loan);

        when(borrowerService.findById(1L)).thenReturn(borrower);

        String result = adminService.recordPayment(request);

        assertEquals("Payment recorded successfully", result);
        verify(paymentService).recordPayment(eq(loan), eq(new BigDecimal("500")), eq("Monthly pay"), eq("ADMIN"));
    }

    @Test
    void recordPayment_NoLoan_ThrowsException() {

        RecordPaymentRequest request = new RecordPaymentRequest(1L, new BigDecimal("500"), "Monthly pay");
        Borrower borrower = new Borrower();

        when(borrowerService.findById(1L)).thenReturn(borrower);

        assertThrows(IllegalStateException.class, () -> adminService.recordPayment(request));
    }

    @Test
    void getBorrowerDetails_Success() {

        Borrower borrower = new Borrower();
        borrower.setId(1L);
        Loan loan = new Loan();
        loan.setId(10L);
        borrower.setLoan(loan);

        when(borrowerService.findById(1L)).thenReturn(borrower);
        when(paymentRepository.findByLoanIdOrderByPaymentDateDesc(10L)).thenReturn(Collections.emptyList());
        when(paymentRepository.findDistinctPaymentDatesByLoanId(10L)).thenReturn(Collections.emptyList());

        BorrowerDetailResponse result = adminService.getBorrowerDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.borrowerId());
    }
}
