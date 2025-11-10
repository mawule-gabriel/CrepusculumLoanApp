package com.crepusculum.loanapp.travel_loan_manager.repository;


import com.crepusculum.loanapp.travel_loan_manager.entity.Guarantor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {
}