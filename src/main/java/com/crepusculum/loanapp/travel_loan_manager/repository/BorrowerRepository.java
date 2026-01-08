package com.crepusculum.loanapp.travel_loan_manager.repository;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByPhoneNumber(String phoneNumber);

    Optional<Borrower> findByGhanaCardNumber(String ghanaCardNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByGhanaCardNumber(String ghanaCardNumber);

    @Query("SELECT b FROM Borrower b WHERE " +
            "LOWER(b.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "b.phoneNumber LIKE CONCAT('%', :term, '%') OR " +
            "b.ghanaCardNumber LIKE CONCAT('%', :term, '%')")
    Page<Borrower> findBySearchTerm(@Param("term") String term, Pageable pageable);
}