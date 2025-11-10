package com.crepusculum.loanapp.travel_loan_manager.repository;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByGhanaCardNumber(String ghanaCardNumber);
}
