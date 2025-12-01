package com.crepusculum.loanapp.travel_loan_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "borrowers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ghana_card_number"),
        @UniqueConstraint(columnNames = "phone_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "ghana_card_number", unique = true, nullable = false)
    private String ghanaCardNumber;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "home_address_ghana", nullable = false)
    private String homeAddressGhana;

    @Column(name = "destination_address", nullable = false)
    private String destinationAddress;

    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @OneToOne(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Loan loan;

    @OneToOne(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Guarantor guarantor;

    @Enumerated(EnumType.STRING)
    private Role role = Role.BORROWER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}