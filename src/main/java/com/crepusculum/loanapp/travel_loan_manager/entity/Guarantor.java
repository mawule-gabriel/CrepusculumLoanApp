package com.crepusculum.loanapp.travel_loan_manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guarantors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "relationship")
    private String relationship;

    @OneToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;
}
