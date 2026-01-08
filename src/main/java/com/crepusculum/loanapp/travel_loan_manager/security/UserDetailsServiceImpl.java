package com.crepusculum.loanapp.travel_loan_manager.security;

import com.crepusculum.loanapp.travel_loan_manager.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BorrowerRepository borrowerRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return borrowerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.error("Failed to find user with phone number: '{}'", phoneNumber);
                    return new UsernameNotFoundException("User not found: " + phoneNumber);
                });
    }
}
