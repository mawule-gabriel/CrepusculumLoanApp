package com.crepusculum.loanapp.travel_loan_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "monthly_payment", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "total_paid", precision = 12, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "balance", precision = 12, scale = 2)
    private BigDecimal balance;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "months_duration", nullable = false)
    private Integer monthsDuration = 11;

    @OneToOne
    @JoinColumn(name = "borrower_id", unique = true, nullable = false)
    private Borrower borrower;

    @PrePersist
    @PreUpdate
    private void calculateBalance() {
        if (amount != null && totalPaid != null) {
            this.balance = amount.subtract(totalPaid);
        }
    }
}
