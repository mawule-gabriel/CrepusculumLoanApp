package com.crepusculum.loanapp.travel_loan_manager.service;

import com.crepusculum.loanapp.travel_loan_manager.dto.response.BorrowerSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowerQueryService {

    private final AdminDashboardService adminDashboardService;

    private static final String DEFAULT_STATUS_FILTER = "On Track,Delayed,Completed";

    public Page<BorrowerSummaryResponse> searchBorrowers(
            String search,
            String status,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        String statusFilter = determineStatusFilter(status);

        return adminDashboardService.searchBorrowers(
                search, statusFilter, page, size, sortBy, sortDir);
    }

    private String determineStatusFilter(String status) {
        if (status != null && !status.isEmpty()) {
            return status;
        }
        return DEFAULT_STATUS_FILTER;
    }
}