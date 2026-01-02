package com.crepusculum.loanapp.travel_loan_manager.config;

import com.crepusculum.loanapp.travel_loan_manager.entity.Borrower;
import com.crepusculum.loanapp.travel_loan_manager.entity.Role;
import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final BorrowerRepository borrowerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.full-name}")
    private String adminFullName;

    @Value("${admin.ghana-card}")
    private String adminGhanaCard;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (borrowerRepository.existsByPhoneNumber(adminPhone) ||
                borrowerRepository.existsByGhanaCardNumber(adminGhanaCard)) {
            log.info("Admin user already exists. Skipping admin creation.");
            return;
        }

        Borrower admin = Borrower.builder()
                .fullName(adminFullName)
                .phoneNumber(adminPhone)
                .ghanaCardNumber(adminGhanaCard)
                .homeAddressGhana("System")
                .destinationAddress("System")
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        borrowerRepository.save(admin);
        log.info("=================================================");
        log.info("Admin user created successfully!");
        log.info("Phone Number: {}", adminPhone);
        log.info("IMPORTANT: Change these credentials in production!");
        log.info("=================================================");
    }
}