package com.crepusculum.loanapp.travel_loan_manager.repository;

import com.crepusculum.loanapp.travel_loan_manager.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanIdOrderByPaymentDateDesc(Long loanId);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.loan.id = :loanId")
    BigDecimal getTotalPaidByLoanId(Long loanId);

    List<Payment> findByPaymentDateBetween(LocalDate start, LocalDate end);
}