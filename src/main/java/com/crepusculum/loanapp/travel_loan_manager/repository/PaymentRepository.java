package com.crepusculum.loanapp.travel_loan_manager.repository;

import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanIdOrderByPaymentDateDesc(Long loanId);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.loan.id = :loanId")
    BigDecimal getTotalPaidByLoanId(Long loanId);

    List<Payment> findByPaymentDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT DISTINCT p.paymentDate FROM Payment p WHERE p.loan.id = :loanId ORDER BY p.paymentDate")
    List<LocalDate> findDistinctPaymentDatesByLoanId(@Param("loanId") Long loanId);
}